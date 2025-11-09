package main.util

object Calculos {
    fun costoConDescuento(base: Double, cantidadMascotas: Int, descuento: Double): Double {
        val aplica = cantidadMascotas > 1
        return if (aplica) base * (1 - descuento) else base
    }
    fun dosis(peso: Double, edad: Int): String {
        val dosisMg = when {
            peso < 5 -> 4.0 // criterio: mascotas muy pequeñas, dosis fija
            peso < 15 -> 10.0 * (if (edad < 1) 0.8 else 1.0)
            else -> 20.0 * (if (edad < 1) 0.8 else 1.0)
        }
        return "${dosisMg}mg"
    }
}
