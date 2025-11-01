# Sistema de Gestión Veterinaria

## Descripción
Este proyecto es una aplicación de consola desarrollada en Kotlin para gestionar consultas veterinarias. Permite registrar mascotas, dueños, calcular costos con descuentos, verificar disponibilidad de veterinarios, agendar consultas y enviar recordatorios de vacunas y citas.

## Cambios recientes
Se integraron las siguientes mejoras solicitadas por el profesor:

- Centralización de validaciones en `src/Utils.kt` mediante el objeto `Validaciones` con métodos reutilizables:
  - `validarEmail(email: String): Boolean`
  - `validarTelefono(telefono: String): Boolean`
- Uso explícito de colecciones `List`, `Set` y `Map` para organizar datos:
  - `mutableListOf` para `veterinarios`, `consultas` y `mascotas`.
  - `mutableMapOf` para `agendaVeterinarios` (fecha -> horas ocupadas) y `agendaPorVeterinario`.
  - `mutableSetOf` para `nombresVeterinarios` y `especialidadesVeterinarios` (evita duplicados) — añadido en `src/Main.kt`.
- Actualización del README para mapear el cumplimiento de la pauta requerida.

## Características principales (implementadas)
- Modelo orientado a objetos con clases: `Usuario`, `Dueno`, `Veterinario`, `Mascota`, `Consulta`.
- Validaciones centralizadas en `src/Utils.kt` (email y teléfono).
- Registro interactivo de mascotas y dueños con validaciones robustas (nombre, teléfono, email, edad, peso, especie).
- Cálculo de costos y aplicación automática de descuento (15% si se atienden 2 o más mascotas).
- Agenda de veterinarios: verificación de disponibilidad por fecha/hora y asignación de consultas a veterinarios.
- Registro y almacenamiento de consultas en memoria (`consultas`) y agrupación por veterinario (`agendaPorVeterinario`).
- Cálculo de próxima fecha de vacunación en `Mascota` y determinación del tipo de vacuna (semestral/anual) con `when`.
- Función de cálculo de dosis recomendada según peso y edad (`calcularDosis`).
- Envío simulado de recordatorios por email para citas programadas y próximas vacunaciones (uso de validación y `let`/checks para nulls).
- Manejo de errores: `try/catch` para entradas inválidas (fechas/horas) y uso de operadores seguros (`?.`, `?:`).

## Cumplimiento de la pauta solicitada
A continuación se indica cómo el proyecto cumple cada punto de la pauta:

- Implementa flujos de decisión (`if`, `when`) para verificar inventarios o estados:
  - `if` en validaciones de entrada (edad, peso, nombre, teléfono, email, respuesta s/n) y descuentos.
  - `when` en `Mascota.tipoVacuna()` y `calcularDosis()` para lógica de dosis.
- Usa arreglos y colecciones para gestionar datos y `strings` para validaciones y formateo:
  - `List` y `MutableList` para colecciones principales (`veterinarios`, `mascotas`, `consultas`).
  - `Set` (`nombresVeterinarios`, `especialidadesVeterinarios`) para garantizar unicidad.
  - `Map` (`agendaVeterinarios`, `agendaPorVeterinario`) para agendas y agrupación.
  - Validaciones basadas en `String` (regex para email, comprobación de dígitos para teléfono) y plantillas de strings para salidas.
- Crea funciones reutilizables que realicen cálculos o verificaciones:
  - `calcularCostoConDescuento`, `calcularDosis`, `validarEmailConDefault`, `Validaciones.validarEmail`, `Validaciones.validarTelefono`, `verificarDisponibilidad`.
- Organiza datos usando colecciones como `listOf`/`mutableListOf`, `setOf`/`mutableSetOf` y `mapOf`/`mutableMapOf`.
- Diseña clases representativas de las entidades e implementa métodos relevantes para encapsular lógica funcional (`Mascota.calcularProximaVacuna`, `Veterinario.estaDisponible`, `Consulta.generarResumen`, `Dueno.mostrarInformacion`).
- Utiliza `try-catch` para manejo de errores en parsing de fechas/horas y operadores seguros (`?.`, `?:`) para evitar NPEs.
- Genera resúmenes formateados con plantillas de strings (`${variable}`) en `generarResumen` y `generarInformeConsultas`.
- Código limpio y organizado con nombres descriptivos y uso efectivo de expresiones.

## Estructura del proyecto
```
veterinaria/
├── src/
│   ├── Main.kt            # Archivo principal con la lógica interactiva
│   ├── Utils.kt           # Validaciones centralizadas (email, teléfono)
│   └── model/
│       ├── Usuario.kt     # Clase base Usuario
│       ├── Dueno.kt       # Clase Dueno : Usuario
│       ├── Veterinario.kt # Clase Veterinario : Usuario (agenda, disponibilidad)
│       ├── Mascota.kt     # Clase Mascota (vacunación, mostrarInformacion)
│       └── Consulta.kt    # Clase Consulta (costo, estado, resumen)
├── README.md
└── veterinaria.iml        # Configuración de IntelliJ
```

## Cómo ejecutar (modo rápido)
1. Abre el proyecto en IntelliJ IDEA y ejecuta `Main.kt` (Run). El programa es interactivo y solicitará datos por consola.

2. Si prefieres línea de comandos y tienes Kotlin/Java configurado, compila y ejecuta desde el IDE o utiliza las herramientas que tengas para ejecutar un archivo Kotlin en consola.

Ejemplo (desde IntelliJ es la opción recomendada):

- Ejecuta la clase `Main` y sigue las instrucciones en pantalla: registrar mascotas y dueño, elegir fecha/hora para cada consulta, revisar el resumen y ver los recordatorios simulados.

## Sugerencias y próximos pasos (opcionales)
- Añadir persistencia (guardar/leer consultas y usuarios en JSON o base de datos).
- Añadir pruebas unitarias (JUnit) para `Validaciones`, `Mascota` y `Consulta`.
- Implementar interfaz CLI más avanzada o API REST si se desea exponer la lógica.

## Contribuciones
Si deseas contribuir, crea un fork y un pull request. Describe claramente los cambios y añade tests cuando sea posible.

## Licencia
Proyecto educativo — sin licencia específica.
