package main.model

open class Cliente(
    val nombre: String,
    val email: String,
    val telefono: String
) {
    // Desestructuración
    operator fun component1() = nombre
    operator fun component2() = email
    operator fun component3() = telefono

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cliente) return false
        return nombre == other.nombre && email == other.email
    }

    override fun hashCode(): Int = 31 * nombre.hashCode() + email.hashCode()

    override fun toString(): String = "Cliente(nombre='$nombre', email='$email', telefono='$telefono')"
}

