package main.model

import main.annotations.Promocionable

class Medicamento(
    val nombre: String,
    val dosificacion: String,
    val precio: Double,
    var stock: Int,
    @field:Promocionable val aplicaPromocion: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Medicamento) return false
        return nombre.equals(other.nombre, ignoreCase = true) &&
                dosificacion.equals(other.dosificacion, ignoreCase = true)
    }

    override fun hashCode(): Int = 31 * nombre.lowercase().hashCode() + dosificacion.lowercase().hashCode()

    override fun toString(): String = "Medicamento(nombre='$nombre', dosis='$dosificacion', precio=$precio, stock=$stock, promo=$aplicaPromocion)"
}

