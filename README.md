# Sistema de Gestión Veterinaria

## Descripción
Aplicación de consola en Kotlin para gestionar consultas veterinarias. Permite registrar mascotas y dueños, calcular costos con descuentos, verificar disponibilidad, agendar consultas y enviar recordatorios.

## Cambios recientes
- Centralización de validaciones en `src/Utils.kt` con el objeto `Validaciones`:
  - `validarEmail(email: String): Boolean`
  - `validarTelefono(telefono: String): Boolean`
  - `normalizarEmail(email: String?, porDefecto: String = "correo@invalido.com"): String`
- Uso de colecciones para organización de datos:
  - `MutableList` para `veterinarios`, `consultas`, `mascotas`.
  - `MutableMap` para `agendaVeterinarios` (fecha -> horas ocupadas) y `agendaPorVeterinario`.
  - `MutableSet` para `nombresVeterinarios` y `especialidadesVeterinarios` (garantiza unicidad).
- Refactor en `Main.kt` para usar `Validaciones.normalizarEmail` y validaciones centralizadas.
- Se añadió resumen e informe formateado de consultas y recordatorios.

## Cumplimiento de la pauta
- Flujos de decisión (if, when) para verificar estados y lógica:
  - `if` en validaciones de datos, disponibilidad, descuentos y control de flujo s/n.
  - `when` en `Mascota.tipoVacuna()` y en `calcularDosis()`.
- Arreglos/colecciones y strings para validaciones/formateo:
  - `List`/`MutableList` para entidades; `Set` para unicidad; `Map` para agendas.
  - Validaciones de `String` (regex email, dígitos tel) y plantillas de strings en salidas.
- Funciones reutilizables para cálculos/verificaciones:
  - `calcularCostoConDescuento`, `calcularDosis`, `verificarDisponibilidad`, `encontrarVeterinarioDisponible`.
  - En `Validaciones`: `validarEmail`, `validarTelefono`, `normalizarEmail`.
- Organización de datos con `listOf`/`setOf`/`mapOf` y variantes mutables.
- Clases representativas: `Usuario`, `Dueno`, `Veterinario`, `Mascota`, `Consulta` con métodos que encapsulan lógica (`mostrarInformacion`, `estaDisponible`, `calcularProximaVacuna`, `generarResumen`).
- Manejo de errores y null-safety:
  - `try-catch` en parseo de fechas/horas y cálculo de recordatorios.
  - Operadores seguros `?.` y `?:` al acceder a propiedades opcionales.
- Resúmenes claros y profesionales con plantillas de strings (`${variable}`) en `generarResumen` y `generarInformeConsultas`.
- Código limpio y organizado con nombres descriptivos.

## Estructura del proyecto
```
veterinaria/
├── src/
│   ├── Main.kt            # Lógica interactiva, flujos y orquestación
│   ├── Utils.kt           # Validaciones centralizadas
│   └── model/
│       ├── Usuario.kt
│       ├── Dueño.kt (clase Dueno)
│       ├── Veterinario.kt
│       ├── Mascota.kt
│       └── Consulta.kt
├── README.md
└── veterinaria.iml
```

## Cómo ejecutar
- Recomendado: abrir en IntelliJ IDEA y ejecutar `Main.kt`.
- La app es interactiva por consola.

## Próximos pasos sugeridos
- Persistencia de datos (JSON/DB) y pruebas unitarias (JUnit) para `Validaciones`, `Mascota` y `Consulta`.
- Más reglas de negocio en agenda (duraciones, solapamientos por veterinario).

## Licencia
Proyecto educativo.
