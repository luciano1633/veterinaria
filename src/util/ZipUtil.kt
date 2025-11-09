package main.util

import java.io.IOException
import java.nio.file.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ZipUtil {
    @Throws(IOException::class)
    fun zipDir(sourceDir: Path, zipFile: Path) {
        if (!Files.exists(sourceDir)) return
        Files.createDirectories(zipFile.parent)
        Files.newOutputStream(zipFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use { fos ->
            ZipOutputStream(fos).use { zos ->
                Files.walk(sourceDir).use { stream ->
                    stream.filter { Files.isRegularFile(it) }.forEach { file ->
                        val entryName = sourceDir.relativize(file).toString().replace('\\', '/')
                        zos.putNextEntry(ZipEntry(entryName))
                        Files.newInputStream(file).use { input -> input.copyTo(zos) }
                        zos.closeEntry()
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    fun zipProyecto(root: Path, destino: Path, exclusiones: List<Regex> = defaultExclusiones()) {
        if (!Files.exists(root)) return
        Files.createDirectories(destino.parent)
        Files.newOutputStream(destino, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use { fos ->
            ZipOutputStream(fos).use { zos ->
                Files.walk(root).use { stream ->
                    stream.filter { Files.isRegularFile(it) }.forEach { file ->
                        val rel = root.relativize(file).toString().replace('\\', '/')
                        if (exclusiones.any { it.containsMatchIn(rel) }) return@forEach
                        zos.putNextEntry(ZipEntry(rel))
                        Files.newInputStream(file).use { input -> input.copyTo(zos) }
                        zos.closeEntry()
                    }
                }
            }
        }
    }

    private fun defaultExclusiones(): List<Regex> = listOf(
        Regex("^\\.git/"),
        Regex("^build/"),
        Regex("^out/"),
        Regex("^\\.idea/"),
        Regex("^salida/resumen.zip$"),
        Regex("^scripts/empacar_proyecto.ps1$") // opcional excluir script mismo si se desea
    )
}
