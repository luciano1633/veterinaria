package main.util

object Input {
    fun leerEnteroPositivo(prompt: String): Int {
        while (true) {
            println(prompt)
            val n = readLine()?.toIntOrNull()
            if (n != null && n > 0) return n
            println("Entrada inválida. Ingrese un número entero positivo.")
        }
    }

    fun leerTextoValidado(prompt: String, valida: (String) -> Boolean, errorMsg: String): String {
        while (true) {
            println(prompt)
            val s = readLine() ?: ""
            if (valida(s)) return s
            println(errorMsg)
        }
    }

    fun leerOpcionSN(prompt: String): Boolean {
        while (true) {
            println(prompt)
            when (readLine()?.trim()?.lowercase()) {
                "s" -> return true
                "n" -> return false
                else -> println("Opción inválida. Ingrese 's' o 'n'.")
            }
        }
    }
}

