package main.service

import main.model.Dueno
import main.model.Mascota

object ReporteService {
    fun resumen(dueno: Dueno, mascotas: List<Mascota>): String = buildString {
        appendLine("Cliente: ${dueno.nombre} | Tel: ${dueno.telefono} | Email: ${dueno.email}")
        appendLine("Mascotas:")
        mascotas.forEach { m ->
            val prox = m.calcularProximaVacuna()?.toString() ?: "Sin registro"
            appendLine(" - ${m.nombre} (${m.especie}) | Edad: ${m.edad} | Peso: ${m.peso} | Próx: $prox | Tipo: ${m.tipoVacuna()}")
        }
    }
}

