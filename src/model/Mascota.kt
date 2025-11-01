package main.model

import java.time.LocalDate

class Mascota(
    val nombre: String,
    val especie: String,
    val edad: Int,
    val peso: Double,
    var ultimaVacunacion: LocalDate? = null
) {
    fun mostrarInformacion(): String {
        val proxVacuna = if (ultimaVacunacion != null) calcularProximaVacuna() else "Sin registro"
        return "Nombre: $nombre, Especie: $especie, Edad: $edad, Peso: $peso, ProximaVacuna: $proxVacuna"
    }

    fun calcularProximaVacuna(): LocalDate? {
        ultimaVacunacion?.let { fecha ->
            val meses = when (especie.lowercase()) {
                "perro" -> if (edad <= 1) 6 else 12
                "gato" -> if (edad <= 1) 6 else 12
                else -> 12
            }
            return fecha.plusMonths(meses.toLong())
        }
        return null
    }

    fun tipoVacuna(): String {
        return when (especie.lowercase()) {
            "perro" -> when {
                edad <= 1 -> "Semestral"
                edad > 1 -> "Anual"
                else -> "N/A"
            }
            "gato" -> when {
                edad <= 1 -> "Semestral"
                edad > 1 -> "Anual"
                else -> "N/A"
            }
            else -> "Anual"
        }
    }
}
