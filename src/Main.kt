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
import main.util.Formatter
import main.util.Promos
import main.util.ReflectionUtil
import java.nio.file.Paths

val veterinarios = mutableListOf<Veterinario>()
val consultas = mutableListOf<Consulta>()
val agendaPorVeterinario = mutableMapOf<String, MutableList<Consulta>>()
val nombresVeterinarios: MutableSet<String> = mutableSetOf()
val especialidadesVeterinarios: MutableSet<String> = mutableSetOf()

// Funciones auxiliares para limpiar el flujo principal
private fun inicializarVeterinarios() {
    val baseVets = listOf(
        Veterinario("Dr. Gonzalez", "22220001", "General", "gonzalez@vet.com"),
        Veterinario("Dra. Perez", "22220002", "Dermatologia", "perez@vet.com")
    )
    veterinarios.addAll(baseVets)
    nombresVeterinarios.addAll(veterinarios.map { it.nombre })
    especialidadesVeterinarios.addAll(veterinarios.map { it.especialidad })
}

private fun registrarMascotas(numero: Int): List<Mascota> = buildList {
    repeat(numero) { idx ->
        println("Registrando mascota ${idx + 1}:")
        add(registrarMascota())
    }
}

private fun crearConsultasParaMascotas(mascotas: List<Mascota>, dueno: Dueno, costoFinalUnitario: Double) {
    var idCounter = consultas.size + 1
    mascotas.forEach { m ->
        val c = Consulta(idCounter++, "Consulta general - ${m.nombre}", costoFinalUnitario)
        c.dueno = dueno
        c.mascota = m
        consultas.add(c)
    }
}

private fun asignarConsultasPendientes(agendaService: AgendaService) {
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
}

private fun runAdvancedDemo() {
    println("\n=== DEMO: Actividad avanzada ===")
    val emailDemo = "demo@dominio.com"
    println("Email '$emailDemo' válido? ${Validaciones.validarEmail(emailDemo)}")
    val telRaw = "12345678"
    val telFmt = Formatter.formatearTelefonoEstandar(telRaw)
    println("Teléfono formateado: $telFmt")
    Promos.inicioPromo = LocalDate.now().minusDays(5)
    Promos.finPromo = LocalDate.now().plusDays(5)
    val cliente = Cliente("Carla", "carla@mail.com", "+56 9 9999 9999")
    val med1 = Medicamento("Amoxicilina", "250mg", 3000.0, stock = 10, aplicaPromocion = true)
    val med2 = Medicamento("Amoxicilina", "250mg", 3000.0, stock = 20, aplicaPromocion = false)
    val med3 = Medicamento("Ivermectina", "10mg", 4500.0, stock = 5, aplicaPromocion = true)
    val setMedicamentos = mutableSetOf(med1, med2, med3) // hash/equals evita duplicado de med2
    val p1 = Pedido(cliente, mutableListOf(ItemPedido(med1, 2)))
    val p2 = Pedido(cliente, mutableListOf(ItemPedido(med3, 1)))
    p1.recalcularTotal(); p2.recalcularTotal()
    val p3 = p1 + p2
    val (nombreCliente, correoCliente, telefonoCliente) = cliente
    println("Cliente desestructurado: $nombreCliente | $correoCliente | $telefonoCliente")
    val (cPedido, itemsPedido, totalPedido) = p3
    println("Pedido desestructurado: cliente=${cPedido.nombre}, items=${itemsPedido.size}, total=$totalPedido")
    println("Reflection Cliente:\n" + ReflectionUtil.describir(cliente))
    println("Reflection Pedido:\n" + ReflectionUtil.describir(p3))
    println("Ingrese cantidad de productos (1-100):")
    val cantidadIngresada = readLine()?.toIntOrNull()
    if (cantidadIngresada == null) println("Cantidad inválida (no es un número)")
    else println(if (cantidadIngresada in 1..100) "Cantidad $cantidadIngresada dentro de rango (1-100)" else "Cantidad $cantidadIngresada fuera de rango (1-100)")
    val promocionables = setMedicamentos.filter { it.aplicaPromocion }
    println("Promocionables: ${promocionables.map { it.nombre }.distinct()}")
}

fun main() {
    println("Bienvenido al sistema de la veterinaria.")
    inicializarVeterinarios()
    val agendaRepo = AgendaRepository()
    val agendaService = AgendaService(agendaRepo, veterinarios)
    val numeroMascotas = Input.leerEnteroPositivo("Ingrese el número de mascotas que serán atendidas:")
    val costoFinalUnitario = Calculos.costoConDescuento(Const.COSTO_BASE, numeroMascotas, Const.DESCUENTO_MULTI_MASCOTA)
    val mascotas = registrarMascotas(numeroMascotas)
    val dueno = registrarDueno()
    crearConsultasParaMascotas(mascotas, dueno, costoFinalUnitario)
    asignarConsultasPendientes(agendaService)
    println("\n--- Resumen Profesional ---")
    println(ReporteService.resumen(dueno, mascotas, consultas))
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
    // Empaquetar proyecto completo (entrega) en build/entrega_proyecto.zip
    try {
        val root = Paths.get("") .toAbsolutePath().normalize()
        val zipEntrega = root.resolve("build").resolve("entrega_proyecto.zip")
        ZipUtil.zipProyecto(root, zipEntrega)
        println("Proyecto empaquetado en: $zipEntrega")
    } catch (e: Exception) {
        println("No se pudo empaquetar el proyecto: ${e.message}")
    }
    runAdvancedDemo()
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
