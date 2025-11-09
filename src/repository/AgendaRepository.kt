package main.repository

class AgendaRepository {
    private val ocupadas = mutableMapOf<String, MutableSet<String>>()

    fun ocupada(fecha: String, hora: String): Boolean = hora in (ocupadas[fecha] ?: emptySet())

    fun reservar(fecha: String, hora: String) {
        ocupadas.getOrPut(fecha) { mutableSetOf() }.add(hora)
    }
}

