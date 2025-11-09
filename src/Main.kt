package main

import java.time.LocalDate
import java.time.LocalTime
import java.time.DayOfWeek
import main.model.*
import main.repository.AgendaRepository
import main.service.AgendaService
import main.service.ReporteService
import main.util.Parsers
import main.util.ZipUtil
import main.util.Const
import main.util.Calculos
import main.util.Input
import java.nio.file.Paths

val veterinarios = mutableListOf<Veterinario>()
val consultas = mutableListOf<Consulta>()
val agendaPorVeterinario = mutableMapOf<String, MutableList<Consulta>>()
val nombresVeterinarios: MutableSet<String> = mutableSetOf()
val especialidadesVeterinarios: MutableSet<String> = mutableSetOf()

fun main() {
    println("Bienvenido al sistema de la veterinaria.")
    // Veterinarios iniciales inmutables base
    val baseVets = listOf(
        Veterinario("Dr. Gonzalez", "22220001", "General", "gonzalez@vet.com"),
        Veterinario("Dra. Perez", "22220002", "Dermatologia", "perez@vet.com")
    )
    veterinarios.addAll(baseVets)
    nombresVeterinarios.addAll(veterinarios.map { it.nombre })
    especialidadesVeterinarios.addAll(veterinarios.map { it.especialidad })
    val agendaRepo = AgendaRepository()
    val agendaService = AgendaService(agendaRepo, veterinarios)
    val numeroMascotas = Input.leerEnteroPositivo("Ingrese el número de mascotas que serán atendidas:")
    val costoFinalUnitario = Calculos.costoConDescuento(Const.COSTO_BASE, numeroMascotas, Const.DESCUENTO_MULTI_MASCOTA)
    val mascotas = mutableListOf<Mascota>()
    repeat(numeroMascotas) { idx ->
        println("Registrando mascota ${idx + 1}:")
        mascotas.add(registrarMascota())
    }
    val dueno = registrarDueno()
    var idCounter = consultas.size + 1
    mascotas.forEach { m ->
        val c = Consulta(idCounter++, "Consulta general - ${m.nombre}", costoFinalUnitario)
        c.dueno = dueno
        c.mascota = m
        consultas.add(c)
    }
    consultas.filter { it.estado == "Pendiente" }.forEach { c ->
        while (true) {
            val (fecha, hora) = solicitarFechaHoraConParsers()
            if (!verificarReglasClinica(fecha, hora)) {
                println("Horario fuera de reglas de la clínica o inválido: $fecha $hora")
                continue
            }
            if (agendaService.asignar(c, fecha, hora)) {
                agendaPorVeterinario.getOrPut(c.veterinario!!.nombre) { mutableListOf() }.add(c)
                println("Consulta ${c.idConsulta} asignada a ${c.veterinario!!.nombre} en $fecha $hora")
                break
            } else {
                if (!Input.leerOpcionSN("No disponible $fecha $hora. ¿Intentar otra? (s/n):")) break
            }
        }
    }
    println("\n--- Resumen Profesional ---")
    val resumen = ReporteService.resumen(dueno, mascotas, consultas)
    println(resumen)
    val ruta = ReporteService.exportar(dueno, mascotas)
    println("Resumen exportado en: $ruta")
    try {
        val zipPath = Paths.get("salida", "resumen.zip")
        ZipUtil.zipDir(Paths.get("salida"), zipPath)
        println("Archivo ZIP generado en: $zipPath")
    } catch (e: Exception) {
        println("No se pudo generar el ZIP: ${e.message}")
    }
    println(ReporteService.informeConsultas(consultas))
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

fun registrarMascota(): Mascota {
    val nombre = Input.leerTextoValidado(
        "Ingrese el nombre de la mascota:",
        { it.isNotBlank() && it.all { ch -> ch.isLetter() || ch == ' ' } },
        "Nombre inválido. Solo letras y espacios."
    )
    val especie = Input.leerTextoValidado(
        "Ingrese la especie (perro/gato):",
        { it.lowercase() in Const.ESPECIES_VALIDAS },
        "Especie no válida. Solo 'perro' o 'gato'."
    ).lowercase()
    val edad = Input.leerEnteroPositivo("Ingrese la edad (en años):")
    val peso = Input.leerTextoValidado(
        "Ingrese el peso (en kg):",
        { it.toDoubleOrNull()?.let { v -> v > 0 } == true },
        "Peso inválido. Debe ser número positivo."
    ).toDouble()
    return Mascota(nombre, especie, edad, peso)
}

fun registrarDueno(): Dueno {
    val nombreDueno = Input.leerTextoValidado(
        "Ingrese el nombre del dueño:",
        { it.isNotBlank() && it.all { ch -> ch.isLetter() || ch == ' ' } },
        "Nombre inválido. Solo letras y espacios."
    )
    val telefono = Input.leerTextoValidado(
        "Ingrese el teléfono (8 dígitos):",
        { Validaciones.validarTelefono(it) },
        "Teléfono inválido. Debe tener exactamente 8 dígitos numéricos."
    )
    println("Ingrese el email (opcional):")
    val emailInput = readLine()
    val email = validarEmailConDefault(emailInput)
    return Dueno(nombreDueno, telefono, email)
}

fun solicitarFechaHoraConParsers(): Pair<String, String> {
    val fecha = solicitarFechaConParsers()
    val hora = solicitarHoraConParsers()
    return fecha to hora
}

fun solicitarFechaConParsers(): String {
    while (true) {
        println("Ingrese la fecha (YYYY-MM-DD):")
        val raw = readLine() ?: ""
        val r = Parsers.fecha(raw)
        if (r.isSuccess) return raw else println("Fecha inválida.")
    }
}

fun solicitarHoraConParsers(): String {
    while (true) {
        println("Ingrese la hora (HH:MM):")
        val raw = readLine() ?: ""
        val r = Parsers.hora(raw)
        if (r.isSuccess) return raw else println("Hora inválida.")
    }
}

fun verificarReglasClinica(fecha: String, hora: String): Boolean {
    return try {
        val date = LocalDate.parse(fecha)
        if (date.dayOfWeek == DayOfWeek.SUNDAY) return false
        val time = LocalTime.parse(hora)
        val start = Const.HORARIO_APERTURA
        val end = Const.HORARIO_CIERRE
        !time.isBefore(start) && !time.isAfter(end)
    } catch (_: Exception) { false }
}
