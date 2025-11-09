package main.model

import main.util.Promos
import java.time.LocalDate

class Pedido(
    val cliente: Cliente,
    val items: MutableList<ItemPedido> = mutableListOf(),
    var fecha: LocalDate = LocalDate.now()
) {
    var total: Double = 0.0
        private set

    fun recalcularTotal() {
        val bruto = items.sumOf { it.medicamento.precio * it.cantidad }
        val descuento = Promos.descuentoPromo(fecha)
        total = bruto * (1 - descuento)
    }

    operator fun plus(other: Pedido): Pedido {
        val combinado = Pedido(this.cliente, this.items.map { it.copy() }.toMutableList(), minOf(this.fecha, other.fecha))
        other.items.forEach { it2 ->
            val existente = combinado.items.find { it.medicamento == it2.medicamento }
            if (existente != null) existente.cantidad += it2.cantidad else combinado.items.add(it2.copy())
        }
        combinado.recalcularTotal()
        return combinado
    }

    // Desestructuración
    operator fun component1() = cliente
    operator fun component2() = items.toList()
    operator fun component3() = total

    override fun toString(): String = "Pedido(cliente=$cliente, items=${items.size}, total=$total, fecha=$fecha)"
}

