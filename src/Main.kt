package main

import java.time.LocalDate
import java.time.LocalTime
import java.time.DayOfWeek
import main.model.*

// Simulación de agenda de veterinarios (puedes expandir con una clase Veterinario)
val agendaVeterinarios = mutableMapOf<String, MutableList<String>>() // Fecha -> Lista de horas ocupadas
val veterinarios = mutableListOf<Veterinario>()
val consultas = mutableListOf<Consulta>()
val agendaPorVeterinario = mutableMapOf<String, MutableList<Consulta>>()

// Conjuntos para garantizar unicidad (Set): nombres y especialidades de veterinarios
val nombresVeterinarios: MutableSet<String> = mutableSetOf()
val especialidadesVeterinarios: MutableSet<String> = mutableSetOf()

fun main() {
    println("Bienvenido al sistema de la veterinaria.")

    // Inicializar algunos veterinarios de ejemplo (nombre, telefono, especialidad, email)
    veterinarios.add(Veterinario("Dr. Gonzalez", "22220001", "General", "gonzalez@vet.com"))
    veterinarios.add(Veterinario("Dra. Perez", "22220002", "Dermatologia", "perez@vet.com"))

    // Actualizar conjuntos a partir de la lista para garantizar unicidad
    nombresVeterinarios.addAll(veterinarios.map { it.nombre })
    especialidadesVeterinarios.addAll(veterinarios.map { it.especialidad })

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
    val dueno = registrarDueno()

    // Crear consulta(s) y asignar dueños/mascotas
    var idCounter = consultas.size + 1
    mascotas.forEach { m ->
        val c = Consulta(idCounter++, "Consulta general - ${m.nombre}", costoFinal)
        // Asignar la misma instancia del dueño registrada
        c.dueno = dueno
        c.mascota = m
        consultas.add(c)
    }

    // Paso 4: Verificación de disponibilidad y asignación de veterinario simple
    consultas.filter { it.estado == "Pendiente" }.forEach { c ->
        var asignada = false
        while (!asignada) {
            val (fecha, hora) = solicitarFechaHora()
            // Verificar disponibilidad general de la clínica (día y horario)
            if (!verificarDisponibilidad(fecha, hora)) {
                println("Horario no disponible para la fecha/hora solicitada: $fecha $hora. Intente otra vez.")
                continue
            }
            val vet = encontrarVeterinarioDisponible(fecha, hora)
            if (vet != null) {
                c.fecha = fecha
                c.hora = hora
                vet.asignarConsulta(c)
                registrarConsulta(fecha, hora)
                agendaPorVeterinario.getOrPut(vet.nombre) { mutableListOf() }.add(c)
                println("Consulta ${c.idConsulta} asignada a ${vet.nombre} en $fecha $hora")
                asignada = true
            } else {
                println("No hay veterinarios disponibles en $fecha $hora. Intente otra vez.")
                var respuesta: String
                while (true) {
                    println("¿Desea intentar con otra fecha/hora? (s/n):")
                    val input = readLine()?.lowercase() ?: ""
                    if (input == "s" || input == "n") {
                        respuesta = input
                        break
                    } else {
                        println("Caracter invalido. Solo se permiten 's' o 'n'.")
                    }
                }
                if (respuesta != "s") {
                    break
                }
            }
        }
    }

    // Resumen final
    generarResumen(dueno, mascotas)

    // Informe de todas las consultas
    generarInformeConsultas()

    // Enviar recordatorios (simulado)
    enviarRecordatorios()
}

fun enviarRecordatorios() {
    println("\n--- Enviando recordatorios ---")
    // Recordatorios de citas programadas
    consultas.filter { it.estado == "Pendiente" && it.fecha != null && it.hora != null }.forEach { c ->
        val email = c.dueno?.email
        if (email != null && Validaciones.validarEmail(email)) {
            println("[MAIL] Recordatorio de cita enviado a $email: Consulta ${c.idConsulta} el ${c.fecha} ${c.hora}")
        } else {
            println("[SKIP] Email inválido para la consulta ${c.idConsulta}, no se envió recordatorio.")
        }
    }

    // Recordatorios de vacunación próximos (en 30 días)
    val hoy = LocalDate.now()
    consultas.forEach { c ->
        val m = c.mascota ?: return@forEach
        val d = c.dueno
        val prox = m.calcularProximaVacuna()
        if (prox != null) {
            try {
                val dias = java.time.temporal.ChronoUnit.DAYS.between(hoy, prox)
                if (dias in 0..30) {
                    val email = d?.email
                    if (email != null && Validaciones.validarEmail(email)) {
                        println("[MAIL] Recordatorio de vacunación enviado a $email: Mascota ${m.nombre} tiene vacunación el $prox")
                    } else {
                        println("[SKIP] Email inválido para vacunación de ${m.nombre}")
                    }
                }
            } catch (_: Exception) {
                // ignore parsing issues
            }
        }
    }
}

// Se reemplaza por la función centralizada en Utils.kt
fun validarEmailConDefault(email: String?): String {
    return Validaciones.normalizarEmail(email)
}

fun calcularDosis(peso: Double, edad: Int): String {
    // Ejemplo simple: dosis en mg según peso y edad
    val factor = when {
        peso < 5 -> 5
        peso < 15 -> 10
        else -> 20
    }
    val ajuste = if (edad < 1) 0.8 else 1.0
    val dosis = factor * ajuste
    return "${dosis}mg"
}

fun encontrarVeterinarioDisponible(fecha: String, hora: String): Veterinario? {
    return veterinarios.find { vet -> vet.estaDisponible(hora, fecha) }
}

fun generarInformeConsultas() {
    println("\n--- Informe de Consultas Registradas ---")
    consultas.forEach { c ->
        println(c.generarResumen())
    }
    val pendientes = consultas.filter { it.estado == "Pendiente" }
    println("\nPendientes (${pendientes.size}):")
    pendientes.forEach { println(it.generarResumen()) }
}

fun generarResumen(dueno: Dueno, mascotas: List<Mascota>) {
    println("\n--- Resumen Final ---")
    println("Datos del Dueno: ${dueno.mostrarInformacion()}")
    mascotas.forEach { m ->
        val prox = m.calcularProximaVacuna()?.toString() ?: "Sin registro"
        val dosis = calcularDosis(m.peso, m.edad)
        println("Mascota: ${m.mostrarInformacion()}, Proxima vacunacion: $prox, Tipo vacuna: ${m.tipoVacuna()}, Dosis recomendada: $dosis")
    }

    // Mostrar consultas del dueno
    println("\nConsultas del dueno:")
    consultas.filter { it.dueno?.nombre == dueno.nombre }.forEach { println(it.generarResumen()) }
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

fun registrarDueno(): Dueno {
    var nombreDueno: String
    while (true) {
        println("Ingrese el nombre del dueño:")
        val input = readLine() ?: ""
        if (input.isNotEmpty() && input.all { it.isLetter() || it == ' ' }) {
            nombreDueno = input
            break
        } else {
            println("Caracter invalido. Solo se permiten letras y espacios.")
        }
    }
    var telefono: String
    while (true) {
        println("Ingrese el telefono:")
        val input = readLine() ?: ""
        if (Validaciones.validarTelefono(input)) {
            telefono = input
            break
        } else {
            println("Numero telefonico invalido. Debe tener exactamente 8 digitos numericos.")
        }
    }
    println("Ingrese el email (opcional):")
    val emailInput = readLine()
    val email = validarEmailConDefault(emailInput)
    return Dueno(nombreDueno, telefono, email)
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
        } catch (_: Exception) {
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
        } catch (_: Exception) {
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
    } catch (_: Exception) {
        false // Fecha o hora inválida
    }
}

fun registrarConsulta(fecha: String, hora: String) {
    agendaVeterinarios.getOrPut(fecha) { mutableListOf() }.add(hora)
}
