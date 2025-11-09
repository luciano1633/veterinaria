package main.util

object ReflectionUtil {
    fun describir(obj: Any): String {
        val cls = obj::class.java
        val sb = StringBuilder()
        sb.appendLine("Clase: ${cls.name}")
        sb.appendLine("Propiedades/Fields:")
        cls.declaredFields.forEach { f -> sb.appendLine(" - ${f.type.simpleName} ${f.name}") }
        sb.appendLine("Métodos:")
        cls.declaredMethods.forEach { m -> sb.appendLine(" - ${m.name}(${m.parameterCount})") }
        return sb.toString()
    }
}
