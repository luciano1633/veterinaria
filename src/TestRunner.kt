package main

import java.time.LocalDate
import main.model.*
import main.repository.AgendaRepository
import main.service.AgendaService
import main.service.ReporteService
import main.util.Calculos
import main.util.Const
import main.util.Parsers
import main.util.Formatter
import main.util.Promos
import main.util.ReflectionUtil
import main.annotations.Promocionable
import kotlin.random.Random

object TestRunner {
    private var passed = 0
    private var failed = 0
    private val failures = mutableListOf<String>()

    private fun assert(name: String, condition: Boolean) {
        if (condition) passed++ else { failed++; failures += name }
    }
    private fun assertEquals(name: String, expected: Any?, actual: Any?) {
        if (expected == actual) passed++ else { failed++; failures += "$name -> esperado=$expected actual=$actual" }
    }

    fun runAll() {
        println("Iniciando tests básicos y avanzados...")
        testValidaciones()
        testCalculos()
        testParsers()
        testAgendaService()
        testReporte()
        // Nuevos tests avanzados
        testFormatter()
        testPromosYPedido()
        testAnotacionesYReflection()
        testOperatorOverloading()
        testDesestructuracion()
        testIgualdadYHashCode()
        testRangosCantidad()
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

    private fun testFormatter() {
        println("[Formatter]")
        val f1 = Formatter.formatearTelefonoEstandar("56987654321")
        assertEquals("Formato exacto 11 dígitos", "+56 (987) 654-321", f1)
        val f2 = Formatter.formatearTelefonoEstandar("12345678")
        val patron = Regex("""^\+\d{2} \(\d{3}\) \d{3}-\d{3,4}$""")
        assert("Formato desde 8 dígitos locales coincide patrón", patron.matches(f2))
    }

    private fun testPromosYPedido() {
        println("[Promos y Pedido]")
        // Configurar rango de promo y verificar descuento aplicado en Pedido
        Promos.inicioPromo = LocalDate.of(2025, 1, 1)
        Promos.finPromo = LocalDate.of(2025, 1, 31)
        Promos.descuentoPromo = 0.10
        val cli = Cliente("Ana", "ana@mail.com", "+56 9 8888 7777")
        val med = Medicamento("Ivermectina", "10mg", 1000.0, stock = 5, aplicaPromocion = true)
        val ped = Pedido(cli, mutableListOf(ItemPedido(med, 1)), fecha = LocalDate.of(2025,1,10))
        ped.recalcularTotal()
        assertEquals("Total con promo 10%", 900.0, ped.total)
        // Fuera de promo
        ped.fecha = LocalDate.of(2025,2,10)
        ped.recalcularTotal()
        assertEquals("Total sin promo", 1000.0, ped.total)
    }

    private fun testAnotacionesYReflection() {
        println("[Anotaciones y Reflection]")
        val med = Medicamento("Amoxicilina", "250mg", 3000.0, stock = 10, aplicaPromocion = true)
        // reflection Java sobre el field de la propiedad
        val fieldJava = med.javaClass.getDeclaredField("aplicaPromocion")
        assert("Campo aplicaPromocion tiene @Promocionable (Java)", fieldJava.isAnnotationPresent(Promocionable::class.java))
        val cli = Cliente("Beto", "beto@mail.com", "+56 9 7777 6666")
        val desc = ReflectionUtil.describir(cli)
        assert("Reflection incluye 'Clase:'", desc.contains("Clase:"))
    }

    private fun testOperatorOverloading() {
        println("[Operator Overloading +]")
        val cli = Cliente("Camila", "cam@mail.com", "+56 9 1234 5678")
        val m1 = Medicamento("A", "1mg", 100.0, 10)
        val m2 = Medicamento("B", "2mg", 200.0, 10)
        val p1 = Pedido(cli, mutableListOf(ItemPedido(m1, 1)))
        val p2 = Pedido(cli, mutableListOf(ItemPedido(m1, 2), ItemPedido(m2, 1)))
        p1.recalcularTotal(); p2.recalcularTotal()
        val p3 = p1 + p2
        val itemM1 = p3.items.find { it.medicamento == m1 }?.cantidad ?: 0
        val itemM2 = p3.items.find { it.medicamento == m2 }?.cantidad ?: 0
        assertEquals("Cantidad combinada m1", 3, itemM1)
        assertEquals("Cantidad combinada m2", 1, itemM2)
        assert("Total recalculado > 0", p3.total > 0)
    }

    private fun testDesestructuracion() {
        println("[Desestructuración]")
        val cli = Cliente("Diego", "d@mail.com", "+56 9 0000 1111")
        val (n,e,t) = cli
        assertEquals("Nombre por destructuring", cli.nombre, n)
        assertEquals("Email por destructuring", cli.email, e)
        assertEquals("Teléfono por destructuring", cli.telefono, t)
        val m = Medicamento("C", "1mg", 50.0, 1)
        val p = Pedido(cli, mutableListOf(ItemPedido(m, 2)))
        p.recalcularTotal()
        val (c, items, tot) = p
        assertEquals("Cliente en pedido (destructuring)", cli, c)
        assertEquals("Items size (destructuring)", 1, items.size)
        assert("Total > 0 (destructuring)", tot > 0)
    }

    private fun testIgualdadYHashCode() {
        println("[Igualdad y hashCode]")
        val c1 = Cliente("Eva", "e@mail.com", "11111111")
        val c2 = Cliente("Eva", "e@mail.com", "22222222") // mismo nombre+email
        assert("Clientes iguales por nombre+email", c1 == c2)
        val setClientes = mutableSetOf(c1, c2)
        assertEquals("Set sin duplicados clientes", 1, setClientes.size)
        val med1 = Medicamento("X", "5mg", 10.0, 1)
        val med2 = Medicamento("x", "5mg", 10.0, 2)
        assert("Medicamentos iguales por nombre+dosis (case-insensitive)", med1 == med2)
        val setMed = mutableSetOf(med1, med2)
        assertEquals("Set sin duplicados medicamentos", 1, setMed.size)
    }

    private fun testRangosCantidad() {
        println("[Rangos cantidad]")
        val dentro = Random.nextInt(1,101) // 1..100 inclusive
        val fuera = 150
        assert("Valor dentro de rango", dentro in 1..100)
        assert("Valor fuera de rango", fuera !in 1..100)
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
