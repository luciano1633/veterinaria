package main.model

class Veterinario(
    nombre: String,
    telefono: String,
    val especialidad: String,
    email: String
) : Usuario(nombre, telefono, email) {
    val agenda = mutableListOf<Consulta>()

    fun estaDisponible(hora: String, fecha: String): Boolean {
        return agenda.none { it.fecha == fecha && it.hora == hora && it.estado == "Pendiente" }
    }

    fun asignarConsulta(consulta: Consulta) {
        agenda.add(consulta)
        consulta.veterinario = this
    }

    override fun mostrarInformacion(): String = "Nombre: $nombre, Telefono: $telefono, Especialidad: $especialidad, Email: $email"
}
