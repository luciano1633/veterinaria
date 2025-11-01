package main.model

class Consulta(
    val idConsulta: Int,
    val descripcion: String,
    val costoConsulta: Double,
    var estado: String = "Pendiente",
    var dueno: Dueno? = null,
    var mascota: Mascota? = null,
    var veterinario: Veterinario? = null,
    var fecha: String? = null,
    var hora: String? = null
) {
    fun calcularCostoFinal(descuento: Double = 0.0): Double {
        return costoConsulta * (1 - descuento)
    }

    fun marcarRealizada() {
        estado = "Realizada"
    }

    fun generarResumen(): String {
        val d = dueno?.nombre ?: "Desconocido"
        val m = mascota?.nombre ?: "Desconocida"
        val vet = veterinario?.nombre ?: "Sin asignar"
        val f = fecha ?: "Sin fecha"
        val h = hora ?: "Sin hora"
        return "Consulta #$idConsulta - Dueño: $d, Mascota: $m, Motivo: $descripcion, Fecha: $f $h, Costo: ${calcularCostoFinal()}, Estado: $estado, Veterinario: $vet"
    }
}
