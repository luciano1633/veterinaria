package main.util

import java.io.IOException
import java.nio.file.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ZipUtil {
    @Throws(IOException::class)
    fun zipDir(sourceDir: Path, zipFile: Path) {
        if (!Files.exists(sourceDir)) return
        Files.newOutputStream(zipFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use { fos ->
            ZipOutputStream(fos).use { zos ->
                Files.walk(sourceDir).use { stream ->
                    stream.filter { Files.isRegularFile(it) }.forEach { file ->
                        val entryName = sourceDir.relativize(file).toString().replace('\\', '/')
                        val entry = ZipEntry(entryName)
                        zos.putNextEntry(entry)
                        Files.newInputStream(file).use { input ->
                            input.copyTo(zos)
                        }
                        zos.closeEntry()
                    }
                }
            }
        }
    }
}

