package main.service

import main.model.Dueno
import main.model.Mascota
import main.model.Consulta
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object ReporteService {
    fun resumen(dueno: Dueno, mascotas: List<Mascota>, consultas: List<Consulta> = emptyList()): String = buildString {
        appendLine("=== Resumen de Cliente ===")
        appendLine("Cliente: ${dueno.nombre} | Tel: ${dueno.telefono} | Email: ${dueno.email}")
        appendLine()
        appendLine("=== Mascotas (${mascotas.size}) ===")
        mascotas.forEach { m ->
            val prox = m.calcularProximaVacuna()?.toString() ?: "Sin registro"
            // nota: dosis simple; si no quieres, remover
            val dosis = main.util.Calculos.dosis(m.peso, m.edad)
            appendLine(" - ${m.nombre} (${m.especie}) | Edad: ${m.edad} | Peso: ${m.peso} | Próx: $prox | Tipo: ${m.tipoVacuna()} | Dosis: $dosis")
        }
        if (consultas.isNotEmpty()) {
            appendLine()
            appendLine("=== Consultas (${consultas.size}) ===")
            val total = consultas.sumOf { it.calcularCostoFinal() }
            val pend = consultas.count { it.estado == "Pendiente" }
            val real = consultas.count { it.estado == "Realizada" }
            appendLine("Totales: monto=$total | pendientes=$pend | realizadas=$real")
            val porVet = consultas.groupBy { it.veterinario?.nombre ?: "Sin asignar" }
            porVet.forEach { (vet, lista) ->
                appendLine(" * $vet: ${lista.size} consultas")
            }
        }
    }

    // Exporta el resumen a un archivo en la carpeta indicada (por defecto 'salida/resumen.txt')
    fun exportar(dueno: Dueno, mascotas: List<Mascota>, directorio: String = "salida", nombreArchivo: String = "resumen.txt"): Path {
        val contenido = resumen(dueno, mascotas)
        val dirPath = Paths.get(directorio)
        Files.createDirectories(dirPath)
        val filePath = dirPath.resolve(nombreArchivo)
        Files.writeString(filePath, contenido)
        return filePath
    }
}
