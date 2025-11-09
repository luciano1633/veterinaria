# Sistema de Gestión Veterinaria

## Descripción
Aplicación de consola en Kotlin para gestionar consultas veterinarias. Permite registrar mascotas y dueños, calcular costos con descuentos, verificar disponibilidad, agendar consultas y enviar recordatorios.

## Arquitectura Actual (Refactor según retroalimentación)
Se segmentó la lógica para mejorar mantenibilidad:
- `model/` contiene entidades (POJOs/Kotlin classes).
- `repository/AgendaRepository.kt` gestiona horarios ocupados (persistencia en memoria).
- `service/AgendaService.kt` asigna consultas aplicando reglas de disponibilidad.
- `service/ReporteService.kt` centraliza la generación de resúmenes profesionales (string builder).
- `util/Parsers.kt` reduce uso de try/catch mediante `runCatching` para fecha/hora.
- `Utils.kt` centraliza validaciones (email, teléfono, normalización).
- `Main.kt` orquesta el flujo, delegando lógica de negocio a servicios y utilidades.

## Cambios recientes
- Centralización de validaciones en `Validaciones` (`validarEmail`, `validarTelefono`, `normalizarEmail`).
- Refactor: se crean `AgendaRepository`, `AgendaService`, `Parsers`, `ReporteService` para elevar niveles de evaluación (funciones reutilizables, manejo de errores, resumen profesional, organización de código).
- Uso ampliado de colecciones: `MutableList`, `MutableSet`, `MutableMap` y acceso seguro.
- Resumen profesional único vía `ReporteService.resumen`.

## Resultados esperados vs rúbrica del profesor
Niveles apuntados tras el refactor:
- Flujos de decisión (if/when) + colecciones y strings: CL.
- Funciones reutilizables y colecciones (listOf, set, map): buscar elevar a CL (se añadieron servicios especializados y repositorio).
- Clases representativas y encapsulación: CL (servicios y repositorio refuerzan el diseño).
- Manejo de errores y null-safety (try-catch, runCatching, operadores seguros): mejorar a CL (introducción de Parsers reduce excepciones manuales y normaliza parseo).
- Resumen profesional con formateo: subir a CL (uso de `buildString` en `ReporteService`).
- Código limpio y organizado: subir de ML a L/CL (separación de responsabilidades y nombres consistentes).

## Cumplimiento de la pauta (detalle)
- Flujos de decisión: `if` y `when` en dosis, tipo de vacuna, validaciones y reglas de clínica.
- Uso de colecciones y strings: listas para entidades, mapas para agenda, sets para unicidad, regex y plantillas `${}` en reportes.
- Funciones reutilizables: métodos en servicios (`AgendaService.asignar`), repositorio (`ocupar/reservar`), utilidades (`Parsers`, `Validaciones`), cálculo (`calcularCostoConDescuento`, `calcularDosis`).
- Organización: separación clara en paquetes (`model`, `repository`, `service`, `util`).
- Clases representativas: `Usuario`, `Dueno`, `Veterinario`, `Mascota`, `Consulta`, más servicios y repositorio.
- Manejo de errores: `runCatching` en `Parsers`, try/catch aislado, operadores seguros `?.` y Elvis `?:`.
- Resumen profesional: `ReporteService.resumen` produce salida estructurada.
- Código limpio: responsabilidades distribuidas, menor densidad en `Main.kt`.

## Estructura del proyecto
```
veterinaria/
├── src/
│   ├── Main.kt
│   ├── Utils.kt
│   ├── model/
│   │   ├── Usuario.kt
│   │   ├── Dueño.kt (Dueno)
│   │   ├── Veterinario.kt
│   │   ├── Mascota.kt
│   │   └── Consulta.kt
│   ├── repository/
│   │   └── AgendaRepository.kt
│   ├── service/
│   │   ├── AgendaService.kt
│   │   └── ReporteService.kt
│   └── util/
│       └── Parsers.kt
├── README.md
└── veterinaria.iml
```

## Cómo ejecutar
- Abrir en IntelliJ IDEA y ejecutar `Main.kt`.
- Seguir prompts: registrar mascotas, dueño, asignar horarios, ver resumen e informes.

## Próximos pasos sugeridos
- Persistencia (Archivos JSON o BD ligera).
- Tests unitarios (JUnit) para `Validaciones`, `AgendaService`, `Parsers`, `ReporteService`.
- Manejo de estados avanzados (cancelada, reprogramada) en `Consulta`.
- Exportar resumen a archivo (TXT/JSON) y empaquetar ZIP automático.

## Licencia
Proyecto educativo.
