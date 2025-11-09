package main.util

object Calculos {
    fun costoConDescuento(base: Double, cantidadMascotas: Int, descuento: Double): Double {
        val aplica = cantidadMascotas > 1
        return if (aplica) base * (1 - descuento) else base
    }
    fun dosis(peso: Double, edad: Int): String {
        val factor = when {
            peso < 5 -> 5
            peso < 15 -> 10
            else -> 20
        }
        val ajuste = if (edad < 1) 0.8 else 1.0
        return "${factor * ajuste}mg"
    }
}

