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

## Actividad Avanzada: Pasos 1–6 Integrados
Esta sección documenta la extensión de la aplicación para cubrir los puntos avanzados solicitados.

### Paso 1: Regex y Ranges
- Validación de correo electrónico: `Validaciones.validarEmail()` (regex estándar nombre@dominio.tld).
- Formateo uniforme de teléfono: `Formatter.formatearTelefonoEstandar(digitos)` transforma entradas locales (8 dígitos) a formato internacional `+XX (XXX) XXX-XXXX`.
- Rango de fechas de promoción: objeto `Promos` mantiene `inicioPromo..finPromo` y aplica `descuentoPromo(fecha)` si la fecha de un pedido cae dentro del rango.
- Validación de cantidad dinámica (1–100): en `Main.kt` se solicita al usuario ingresar la cantidad y se verifica con `if (cantidadIngresada in 1..100)`.

### Paso 2: Anotaciones y Reflection
- Anotación personalizada `@Promocionable` aplicada a la propiedad `aplicaPromocion` en `Medicamento`.
- Identificación dinámica de medicamentos promocionables filtrando `med.aplicaPromocion`.
- Reflection: `ReflectionUtil.describir(obj)` lista campos y métodos de instancias (`Cliente`, `Pedido`) mostrando estructura de la clase en tiempo de ejecución.

### Paso 3: Operator Overloading
- Operador `+` sobrecargado en `Pedido` combina items de dos pedidos, suma cantidades y recalcula total con posible descuento por rango promocional.
- Comparación de `Medicamento` por nombre y dosificación a través de `equals()` (permite usar `==` y evitar duplicados en `Set`).

### Paso 4: Desestructuración
- `Cliente` define `component1()`, `component2()`, `component3()` para extraer `nombre`, `email`, `telefono` directamente: `val (n,e,t) = cliente`.
- `Pedido` define `component1()`, `component2()`, `component3()` para obtener `cliente`, `items`, `total`: `val (c, items, tot) = pedido`.

### Paso 5: Igualdad y hashCode
- `Cliente.equals()` y `hashCode()` basados en `nombre + email` (evita repetición de clientes).
- `Medicamento.equals()` y `hashCode()` basados en `nombre + dosificación` (case-insensitive) para evitar duplicados en inventario.
- Uso efectivo: al añadir medicamentos a `mutableSetOf<Medicamento>()` solo se conserva uno si comparten clave lógica.

### Paso 6: Resumen y Ejecución Integral
Al ejecutar `Main.kt`:
1. Se gestionan veterinarios, mascotas y consultas (flujo base OOP semana previa).
2. Se genera resumen profesional (`ReporteService.resumen`) y archivo `salida/resumen.txt` + ZIP.
3. Se ejecuta la DEMO avanzada (impresiones en consola) mostrando:
   - Validación de email y formateo de teléfono.
   - Rango de promociones aplicado.
   - Operador `+` combinando dos pedidos.
   - Desestructuración de `Cliente` y `Pedido`.
   - Reflection de `Cliente` y `Pedido`.
   - Filtrado de promocionables vía anotación.
   - Validación de rango de cantidad ingresada.
4. Se imprime listado de medicamentos únicos por igualdad.

### Ubicación de Código Clave
- Regex / teléfono: `src/util/Formatter.kt`, `src/Utils.kt`.
- Anotaciones: `src/annotations/Promocionable.kt`.
- Reflection: `src/util/ReflectionUtil.kt`.
- Overloading +: `src/model/Pedido.kt`.
- Igualdad y desestructuración: `src/model/Cliente.kt`, `src/model/Medicamento.kt`, `src/model/Pedido.kt`.
- Ranges promoción: `src/util/Promos.kt`.
- DEMO integrada: `src/Main.kt` (sección "DEMO Actividad avanzada").

### Ejecución Rápida de la DEMO
Ejecutar `Main.kt` y seguir los prompts (número de mascotas, datos de cada mascota, datos de dueño, cantidad de productos en DEMO avanzada).

### Pruebas Básicas
`TestRunner.kt` incluye tests mínimos (Validaciones, Calculos, Parsers, AgendaService, ReporteService). Ejecutar ese archivo para ver resumen PASSED/FAILED.

## Próximos pasos
- Añadir pruebas unitarias (JUnit) para `Validaciones`, `Parsers`, `AgendaService`, `ReporteService`.
- Persistencia (JSON/DB) para consultas y usuarios.
- Estados avanzados de consulta (cancelada, reprogramada) y validaciones extra.

## Licencia
Proyecto educativo.
