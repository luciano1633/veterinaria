package main

import main.model.*
import main.repository.AgendaRepository
import main.service.AgendaService
import main.service.ReporteService
import main.util.Calculos
import main.util.Const
import main.util.Parsers

object TestRunner {
    private var passed = 0
    private var failed = 0
    private val failures = mutableListOf<String>()

    private fun assert(name: String, condition: Boolean) {
        if (condition) {
            passed++
        } else {
            failed++
            failures += name
        }
    }

    private fun assertEquals(name: String, expected: Any?, actual: Any?) {
        assert(name, expected == actual)
        if (expected != actual) {
            failures += "$name -> esperado=$expected actual=$actual"
        }
    }

    fun runAll() {
        println("Iniciando tests básicos...")
        testValidaciones()
        testCalculos()
        testParsers()
        testAgendaService()
        testReporte()
        resumen()
    }

    private fun testValidaciones() {
        println("[Validaciones]")
        assert("Email válido", Validaciones.validarEmail("user@test.com"))
        assert("Email inválido sin @", !Validaciones.validarEmail("usertest.com"))
        assert("Teléfono válido", Validaciones.validarTelefono("12345678"))
        assert("Teléfono inválido letras", !Validaciones.validarTelefono("12ab5678"))
    }

    private fun testCalculos() {
        println("[Calculos]")
        val costo1 = Calculos.costoConDescuento(Const.COSTO_BASE, 1, Const.DESCUENTO_MULTI_MASCOTA)
        val costo2 = Calculos.costoConDescuento(Const.COSTO_BASE, 2, Const.DESCUENTO_MULTI_MASCOTA)
        assertEquals("Costo sin descuento (1 mascota)", Const.COSTO_BASE, costo1)
        assertEquals("Costo con descuento (2 mascotas)", Const.COSTO_BASE * (1 - Const.DESCUENTO_MULTI_MASCOTA), costo2)
        assertEquals("Dosis <5kg", "4.0mg", Calculos.dosis(4.0, 2))
        assertEquals("Dosis cachorro <1 año ajuste", "4.0mg", Calculos.dosis(4.0, 0))
        assertEquals("Dosis entre 5 y 15", "10.0mg", Calculos.dosis(10.0, 2))
        assertEquals("Dosis >15", "20.0mg", Calculos.dosis(20.0, 2))
    }

    private fun testParsers() {
        println("[Parsers]")
        assert("Fecha válida", Parsers.fecha("2025-01-01").isSuccess)
        assert("Fecha inválida", Parsers.fecha("2025-13-40").isFailure)
        assert("Hora válida", Parsers.hora("10:30").isSuccess)
        assert("Hora inválida", Parsers.hora("25:61").isFailure)
    }

    private fun testAgendaService() {
        println("[AgendaService]")
        val vet = Veterinario("Dr Test", "22223333", "General", "test@vet.com")
        val repo = AgendaRepository()
        val service = AgendaService(repo, listOf(vet))
        val consulta1 = Consulta(1, "Revisión", 5000.0)
        val ok1 = service.asignar(consulta1, "2025-01-10", "10:00")
        assert("Asignación inicial correcta", ok1 && consulta1.veterinario == vet)
        val consulta2 = Consulta(2, "Revisión", 5000.0)
        val ok2 = service.asignar(consulta2, "2025-01-10", "10:00")
        assert("Segunda asignación mismo horario falla", !ok2)
    }

    private fun testReporte() {
        println("[ReporteService]")
        val dueno = Dueno("Luis", "12345678", "luis@mail.com")
        val masc = listOf(Mascota("Fido", "perro", 2, 10.0))
        val consulta = Consulta(1, "Chequeo", 5000.0)
        consulta.dueno = dueno
        consulta.mascota = masc[0]
        val rep = ReporteService.resumen(dueno, masc, listOf(consulta))
        assert("Resumen contiene nombre dueño", rep.contains("Luis"))
        assert("Resumen contiene nombre mascota", rep.contains("Fido"))
        assert("Resumen contiene sección Consultas", rep.contains("=== Consultas"))
    }

    private fun resumen() {
        println("\nResultado tests: PASSED=$passed FAILED=$failed")
        if (failed > 0) {
            println("Fallos:")
            failures.forEach { println(" - $it") }
        }
        println("Fin de ejecución de TestRunner.")
    }
}

fun main() {
    // Ejecutar solo tests cuando se corre TestRunner.kt explícitamente
    TestRunner.runAll()
}

