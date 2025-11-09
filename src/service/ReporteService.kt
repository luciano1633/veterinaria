package main.service

import main.model.Dueno
import main.model.Mascota
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object ReporteService {
    fun resumen(dueno: Dueno, mascotas: List<Mascota>): String = buildString {
        appendLine("Cliente: ${dueno.nombre} | Tel: ${dueno.telefono} | Email: ${dueno.email}")
        appendLine("Mascotas:")
        mascotas.forEach { m ->
            val prox = m.calcularProximaVacuna()?.toString() ?: "Sin registro"
            appendLine(" - ${m.nombre} (${m.especie}) | Edad: ${m.edad} | Peso: ${m.peso} | Próx: $prox | Tipo: ${m.tipoVacuna()}")
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
