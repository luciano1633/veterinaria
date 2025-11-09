package main.util

import java.time.LocalDate

object Promos {
    // Rango de fechas de promoción (editable)
    var inicioPromo: LocalDate = LocalDate.now().minusDays(7)
    var finPromo: LocalDate = LocalDate.now().plusDays(7)
    var descuentoPromo: Double = 0.10 // 10%

    fun enRango(fecha: LocalDate): Boolean = fecha in inicioPromo..finPromo
    fun descuentoPromo(fecha: LocalDate): Double = if (enRango(fecha)) descuentoPromo else 0.0
}

