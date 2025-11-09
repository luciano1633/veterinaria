package main.util

object Formatter {
    // Formatea a +XX (XXX) XXX-XXXX si el input tiene 11 dígitos; si no, retorna original
    fun formatearTelefonoEstandar(digitos: String, codigoPaisPorDefecto: String = "56"): String {
        val solo = digitos.filter { it.isDigit() }
        if (solo.length == 11) {
            val cc = solo.substring(0, 2)
            val area = solo.substring(2, 5)
            val parte1 = solo.substring(5, 8)
            val parte2 = solo.substring(8, 12)
            return "+$cc ($area) $parte1-$parte2"
        }
        // Si llega con 8 dígitos (formato local), lo convertimos a 11 asumiendo país y área genérica "2"
        if (solo.length == 8) {
            val compuesto = codigoPaisPorDefecto + "2" + solo // ej: 56 + 2 + 8 dígitos = 11
            return formatearTelefonoEstandar(compuesto, codigoPaisPorDefecto)
        }
        return digitos
    }
}

