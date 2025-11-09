package main.util

object Formatter {
    // Formatea a +XX (XXX) XXX-XXXX para 12 dígitos; si son 11, usa XXX-XXX
    fun formatearTelefonoEstandar(digitos: String, codigoPaisPorDefecto: String = "56"): String {
        val solo = digitos.filter { it.isDigit() }
        when (solo.length) {
            12 -> {
                val cc = solo.substring(0, 2)
                val area = solo.substring(2, 5)
                val parte1 = solo.substring(5, 8)
                val parte2 = solo.substring(8, 12)
                return "+$cc ($area) $parte1-$parte2"
            }
            11 -> {
                val cc = solo.substring(0, 2)
                val area = solo.substring(2, 5)
                val parte1 = solo.substring(5, 8)
                val parte2 = solo.substring(8, 11)
                return "+$cc ($area) $parte1-$parte2"
            }
            8 -> {
                // Si llega con 8 dígitos (formato local), lo convertimos a 11 asumiendo país y área genérica "2"
                val compuesto = codigoPaisPorDefecto + "2" + solo // ej: 56 + 2 + 8 dígitos = 11
                return formatearTelefonoEstandar(compuesto, codigoPaisPorDefecto)
            }
            else -> return digitos
        }
    }
}
