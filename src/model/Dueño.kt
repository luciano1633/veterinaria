package main.model

class Dueno(
    nombre: String,
    telefono: String,
    email: String
) : Usuario(nombre, telefono, email) {
    // Se puede añadir más comportamiento específico de Dueno aquí
}
