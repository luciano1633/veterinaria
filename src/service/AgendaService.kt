package main.service

import main.model.Consulta
import main.model.Veterinario
import main.repository.AgendaRepository

class AgendaService(private val repo: AgendaRepository, private val vets: List<Veterinario>) {
    fun asignar(c: Consulta, fecha: String, hora: String): Boolean {
        if (repo.ocupada(fecha, hora)) return false
        val v = vets.firstOrNull { it.estaDisponible(hora, fecha) } ?: return false
        c.fecha = fecha
        c.hora = hora
        v.asignarConsulta(c)
        repo.reservar(fecha, hora)
        return true
    }
}
