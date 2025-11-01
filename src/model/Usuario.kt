package main.model

open class Usuario(
    val nombre: String,
    val telefono: String,
    val email: String
) {
    open fun mostrarInformacion(): String = "Nombre: $nombre, Telefono: $telefono, Email: $email"
}

