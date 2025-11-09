# Sistema de Gestión Veterinaria

## Descripción
Aplicación de consola en Kotlin para gestionar consultas veterinarias. Permite registrar mascotas y dueños, calcular costos con descuentos, verificar disponibilidad, agendar consultas y enviar recordatorios.

## Arquitectura Actual (Refactor según retroalimentación)
- model/: entidades.
- repository/AgendaRepository.kt: horarios ocupados.
- service/AgendaService.kt: asignación de consultas.
- service/ReporteService.kt: resumen profesional y exportación a archivo.
- util/Parsers.kt: parseo seguro de fecha/hora (runCatching).
- util/ZipUtil.kt: generación de ZIP del resumen.
- Utils.kt: validaciones (email/teléfono).
- Main.kt: orquestación del flujo.

## Cambios recientes
- Validaciones centralizadas: `Validaciones` (email, teléfono, normalización).
- Servicios y repositorio: `AgendaService`, `AgendaRepository`.
- Parsers: menos try/catch en Main mediante `runCatching`.
- Reporte profesional y exportación: `ReporteService.resumen/exportar`.
- Generación automática de ZIP: `ZipUtil.zipDir("salida", "salida/resumen.zip")` desde `Main.kt`.

## Cumplimiento de la pauta (y mejora de niveles)
- If/when + colecciones + strings: CL.
- Funciones reutilizables + colecciones (listOf, set, map): L → fortalecidas con servicios/repositorio.
- Clases representativas y encapsulación: CL.
- try-catch + null-safety: L → reforzado con Parsers (`runCatching`) y operadores seguros.
- Resumen claro con plantillas: L → reforzado con `ReporteService` y exportación.
- Código limpio y organizado: ML → mejorado con separación por capas y nombres descriptivos.
- Entrega del resumen en ZIP: ML → ahora automático en `salida/resumen.zip` al ejecutar.

## Ejecución rápida
- Ejecuta `Main.kt` en IntelliJ. Al finalizar:
  - Se muestra el resumen en consola.
  - Se guarda en `salida/resumen.txt`.
  - Se genera `salida/resumen.zip` automáticamente.

## Entrega en formato ZIP (Punto 7)
El criterio de evaluación que exige entregar el código en formato ZIP/RAR se cumple ahora con dos mecanismos:
1. Generación automática del ZIP del resumen (`salida/resumen.zip`) al ejecutar `Main.kt`.
2. Script de empaquetado completo del proyecto (`scripts/empacar_proyecto.ps1`) que crea `build/entrega_proyecto.zip` con el código fuente limpio.

### Uso del script (PowerShell)
Ejecutar desde PowerShell en la raíz del proyecto:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\empacar_proyecto.ps1
```

Opcionalmente indicar otra ruta destino:
```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\empacar_proyecto.ps1 -Destino "build/mi_entrega.zip"
```

Esto genera un archivo ZIP sin incluir `.git`, `.idea`, `build`, `out` ni el propio ZIP del resumen.

### Alternativa RAR (manual)
Si se requiere RAR, se puede usar una herramienta externa (WinRAR / 7-Zip) sobre el contenido del proyecto, o convertir el ZIP generado.

## Próximos pasos
- Añadir pruebas unitarias (JUnit) para `Validaciones`, `Parsers`, `AgendaService`, `ReporteService`.
- Persistencia (JSON/DB) para consultas y usuarios.
- Estados avanzados de consulta (cancelada, reprogramada) y validaciones extra.

## Licencia
Proyecto educativo.
