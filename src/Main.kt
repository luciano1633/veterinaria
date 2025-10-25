package main

import kotlin.system.exitProcess
import java.time.LocalDate
import java.time.LocalTime
import java.time.DayOfWeek
import main.model.*

// Simulación de agenda de veterinarios (puedes expandir con una clase Veterinario)
val agendaVeterinarios = mutableMapOf<String, MutableList<String>>() // Fecha -> Lista de horas ocupadas

fun main() {
    println("Bienvenido al sistema de la veterinaria.")

    // Paso 3: Preguntar número de mascotas primero para el descuento
    var numeroMascotas: Int
    while (true) {
        println("Ingrese el número de mascotas que seran atendidas:")
        val input = readLine()?.toIntOrNull()
        if (input != null && input > 0) {
            numeroMascotas = input
            break
        } else {
            println("Caracter no valido. Por favor ingresar un numero valido.")
        }
    }
    val costoBase = 5000.0 // Costo base por consulta
    val costoFinal = calcularCostoConDescuento(costoBase, numeroMascotas)

    // Paso 2: Entrada de datos para mascotas (repetir si hay más de una)
    val mascotas = mutableListOf<Mascota>()
    for (i in 1..numeroMascotas) {
        println("Registrando mascota $i:")
        val mascota = registrarMascota()
        mascotas.add(mascota)
    }

    // Registrar dueño (uno solo)
    val dueño = registrarDueño()

    // Paso 4: Verificación de disponibilidad
    var disponible = false
    var fecha = ""
    var hora = ""
    while (!disponible) {
        val (f, h) = solicitarFechaHora()
        fecha = f
        hora = h
        disponible = verificarDisponibilidad(fecha, hora)
        if (!disponible) {
            println("El veterinario no está disponible en $fecha a las $hora.")
            println("Días disponibles: Lunes a sabado.")
            println("Horario disponible: 08:00 a 16:00.")
            var respuesta: String
            while (true) {
                println("¿Desea intentar agendar la consulta con una nueva fecha y hora? (s/n):")
                val input = readLine()?.lowercase() ?: ""
                if (input == "s" || input == "n") {
                    respuesta = input
                    break
                } else {
                    println("Caracter invalido. Solo se permiten 's' o 'n'.")
                }
            }
            if (respuesta != "s") {
                exitProcess(0)
            }
        }
    }
    registrarConsulta(fecha, hora)
    println("Consulta registrada exitosamente.")

    // Crear consulta
    val consulta = Consulta(
        idConsulta = 1, // Simulado, en producción usa un generador único
        descripcion = "Consulta general",
        costoConsulta = costoFinal,
        estado = "Pendiente"
    )

    // Paso 6: Resumen
    mostrarResumen(dueño, mascotas, consulta)
}

fun registrarMascota(): Mascota {
    var nombre: String
    while (true) {
        println("Ingrese el nombre de la mascota:")
        val input = readLine() ?: ""
        if (input.isNotEmpty() && input.all { it.isLetter() || it == ' ' }) {
            nombre = input
            break
        } else {
            println("Nombre inválido. Solo se permiten letras y espacios.")
        }
    }
    var especie: String
    while (true) {
        println("Ingrese la especie:")
        val input = readLine()?.lowercase() ?: ""
        if (input == "perro" || input == "gato") {
            especie = input
            break
        } else {
            println("Especie no valida. Solo se permiten 'perro' o 'gato'.")
        }
    }
    var edad: Int
    while (true) {
        println("Ingrese la edad (en años):")
        val input = readLine()?.toIntOrNull()
        if (input != null && input > 0) {
            edad = input
            break
        } else {
            println("Caracter no valido. Por favor ingresar un numero valido.")
        }
    }
    var peso: Double
    while (true) {
        println("Ingrese el peso (en kg):")
        val input = readLine()?.toDoubleOrNull()
        if (input != null && input > 0) {
            peso = input
            break
        } else {
            println("Caracter no valido. Por favor ingresar un numero valido.")
        }
    }
    return Mascota(nombre, especie, edad, peso)
}

fun registrarDueño(): Dueño {
    var nombreDueño: String
    while (true) {
        println("Ingrese el nombre del dueño:")
        val input = readLine() ?: ""
        if (input.isNotEmpty() && input.all { it.isLetter() || it == ' ' }) {
            nombreDueño = input
            break
        } else {
            println("Caracter invalido. Solo se permiten letras y espacios.")
        }
    }
    var telefono: String
    while (true) {
        println("Ingrese el teléfono:")
        val input = readLine() ?: ""
        if (input.length == 8 && input.all { it.isDigit() }) {
            telefono = input
            break
        } else {
            println("Numero telefonico invalido. Debe tener exactamente 8 dígitos numéricos.")
        }
    }
    var email: String
    while (true) {
        println("Ingrese el email:")
        val input = readLine() ?: ""
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        if (input.matches(emailRegex)) {
            email = input
            break
        } else {
            println("Email no valido. Ingrese un email válido (ej. usuario@dominio.com).")
        }
    }
    return Dueño(nombreDueño, telefono, email)
}

fun calcularCostoConDescuento(costoBase: Double, numeroMascotas: Int): Double {
    val descuento = if (numeroMascotas > 1) 0.15 else 0.0
    return costoBase * (1 - descuento)
}

fun solicitarFechaHora(): Pair<String, String> {
    var fecha: String
    while (true) {
        println("Ingrese la fecha (YYYY-MM-DD):")
        val input = readLine() ?: ""
        try {
            LocalDate.parse(input)
            fecha = input
            break
        } catch (e: Exception) {
            println("Fecha incorrecta. Ingrese una fecha válida en formato YYYY-MM-DD.")
        }
    }
    var hora: String
    while (true) {
        println("Ingrese la hora (HH:MM):")
        val input = readLine() ?: ""
        try {
            LocalTime.parse(input)
            hora = input
            break
        } catch (e: Exception) {
            println("hora invalida. Ingrese una hora válida en formato HH:MM.")
        }
    }
    return Pair(fecha, hora)
}

fun verificarDisponibilidad(fecha: String, hora: String): Boolean {
    return try {
        val date = LocalDate.parse(fecha)
        val dayOfWeek = date.dayOfWeek
        if (dayOfWeek == DayOfWeek.SUNDAY) return false

        val time = LocalTime.parse(hora)
        val start = LocalTime.of(8, 0)
        val end = LocalTime.of(16, 0)
        if (time.isBefore(start) || time.isAfter(end)) return false

        val ocupadas = agendaVeterinarios[fecha] ?: mutableListOf()
        hora !in ocupadas
    } catch (e: Exception) {
        false // Fecha o hora inválida
    }
}

fun registrarConsulta(fecha: String, hora: String) {
    agendaVeterinarios.getOrPut(fecha) { mutableListOf() }.add(hora)
}

fun mostrarResumen(dueño: Dueño, mascotas: List<Mascota>, consulta: Consulta) {
    println("\n--- Resumen de la Consulta ---")
    println("Dueño: ${dueño.nombreDueño}, Teléfono: ${dueño.telefono}, Email: ${dueño.email}")
    mascotas.forEachIndexed { index, mascota ->
        println("Mascota ${index + 1}: ${mascota.nombre}, Especie: ${mascota.especie}")
    }
    println("Consulta: ${consulta.descripcion}, Costo final: ${consulta.costoConsulta}, Estado: ${consulta.estado}")
    println("Consulta: agendada exitosamente")
}
