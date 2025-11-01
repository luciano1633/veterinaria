package main

object Validaciones {
    fun validarEmail(email: String): Boolean {
        val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return regex.matches(email)
    }

    fun validarTelefono(telefono: String): Boolean {
        return telefono.length == 8 && telefono.all { it.isDigit() }
    }
}

