package main.util

import java.time.LocalTime

object Const {
    val ESPECIES_VALIDAS = setOf("perro", "gato")
    val HORARIO_APERTURA: LocalTime = LocalTime.of(8, 0)
    val HORARIO_CIERRE: LocalTime = LocalTime.of(16, 0)
    const val COSTO_BASE: Double = 5000.0
    const val DESCUENTO_MULTI_MASCOTA: Double = 0.15
}

