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

## Estado de la DEMO Avanzada
La demostración avanzada (reflection, anotaciones, operador +, desestructuración, igualdad ampliada, formateo telefónico internacional) se mantiene implementada y testeada internamente (TestRunner), pero está deshabilitada en el flujo principal para entregar una experiencia limpia de uso. Si se requiere habilitarla nuevamente, puede añadirse una bandera en `Main.kt` o reintroducir la función `runAdvancedDemo()`.

## Cumplimiento de Criterios de Evaluación
A continuación se describe cómo el proyecto cumple cada criterio al nivel “Completamente Logrado (100%)”.

### 1. Validaciones con Regex y Rangos (CL)
- Email: `Validaciones.validarEmail()` usa regex estándar `nombre@dominio.tld`.
- Teléfono: `Validaciones.validarTelefono()` asegura 8 dígitos locales; formateo internacional disponible en `Formatter.formatearTelefonoEstandar()`.
- Fechas y horas: parsing seguro con `Parsers.fecha()` y `Parsers.hora()` (runCatching) y reglas de clínica (`verificarReglasClinica`) impiden domingos y horarios fuera de 08:00–16:00.
- Cantidades: entrada validada con `Input.leerEnteroEnRango("(1-100)")` con mensajes claros si está fuera de rango.
- Mensajes: cada error de entrada imprime mensaje específico (fecha inválida, hora inválida, teléfono inválido, cantidad fuera de rango).

### 2. Anotaciones y Reflection (CL)
- Anotación `@Promocionable` (archivo `annotations/Promocionable.kt`) aplicada a campo `aplicaPromocion` en `Medicamento` con retention RUNTIME.
- Reflection: `ReflectionUtil.describir(obj)` lista propiedades y métodos de instancias (Cliente, Pedido) para análisis dinámico.
- TestRunner verifica presencia de la anotación vía `field.isAnnotationPresent(Promocionable::class.java)`.
- Manejo seguro: uso de reflection Java sin necesidad de kotlin-reflect (evita errores de classpath).

### 3. Sobrecarga de Operadores (CL)
- `Pedido.operator plus`: combina pedidos, suma cantidades de medicamentos iguales y recalcula totales con descuento promocional condicional.
- Igualdad en `Medicamento` permite comparar objetos por `nombre + dosificación` (case-insensitive) y evitar duplicados en sets.
- Casos especiales: si no encuentra el medicamento se añade; si existe se incrementa cantidad.

### 4. Desestructuración (CL)
- `Cliente`: `component1/2/3` para extraer `nombre`, `email`, `telefono`.
- `Pedido`: `component1/2/3` para extraer `cliente`, `items`, `total`.
- Propósito: facilita acceso directo y legible a componentes en contextos externos (tests / posible UI futura).

### 5. Evaluación de Igualdad (CL)
- `Cliente.equals/hashCode`: clave lógica = `nombre + email`. Previene duplicados al insertar en colecciones tipo Set.
- `Medicamento.equals/hashCode`: clave lógica = `nombre + dosificación` (ignora diferencias en case y stock/precio).
- TestRunner confirma que duplicados no se agregan a sets.

### 6. Resumen e Integración Completa (CL)
- `ReporteService.resumen`: incluye cliente, listado de mascotas con próxima vacuna, tipo de vacuna, dosis calculada (`Calculos.dosis`), totales de consultas (bruto, descuento, neto), conteos por estado y agrupación por veterinario.
- `ReporteService.informeConsultas`: detalle de cada consulta y sección de pendientes.
- Exportación: archivo `salida/resumen.txt` y `salida/resumen.zip` generados automáticamente.
- Recordatorios: envío simulado a emails válidos para citas y vacunaciones próximas (0–30 días).

### 7. Entrega y Formato (CL)
- Empaquetado automático del resumen (`ZipUtil.zipDir("salida", "salida/resumen.zip")`).
- Empaquetado del proyecto completo (`ZipUtil.zipProyecto(...)`) genera `build/entrega_proyecto.zip` excluyendo metadatos y carpetas no requeridas (`.git`, `out`, `build`, `.idea`).
- Estructura de carpetas clara (model, service, repository, util, annotations, scripts).
- README documenta arquitectura, criterios avanzados, pruebas y próximos pasos.
- Script PowerShell opcional (`scripts/empacar_proyecto.ps1`) para empaquetado manual.

### Evidencia de Pruebas (TestRunner)
El archivo `TestRunner.kt` ejerce pruebas sobre:
- Validaciones de email/teléfono.
- Cálculos de descuento y dosis.
- Parsers de fecha/hora.
- Agenda (conflictos de horario).
- Reporte (secciones principales presentes).
- Formatter (patrón internacional teléfono).
- Promos y pedido (descuento aplicado dentro del rango y no fuera).
- Anotaciones y reflection (detección de @Promocionable y descripción de clase).
- Operator overloading (+) y acumulación de cantidades.
- Desestructuración (Cliente y Pedido).
- Igualdad y hashCode (Sets sin duplicados).
- Rangos (valores dentro / fuera).

## Ejecución rápida
- Ejecuta `Main.kt` en IntelliJ. Al finalizar:
  - Resumen profesional y archivo de salida.
  - ZIP del resumen.
  - ZIP del proyecto completo (`build/entrega_proyecto.zip`).
  - Informe de consultas y recordatorios.

## Pruebas Básicas
Ejecutar `TestRunner.kt` para validar funcionalidades (PASSED/FAILED). Actualmente todos los tests pasan.

## Próximos pasos
- Integrar Gradle + JUnit para pruebas formales.
- Persistencia (JSON / base de datos) para historizar consultas y mascotas.
- Logging (slf4j) para reemplazar println.
- Parametrizar horarios y descuentos vía archivo de configuración.

## Licencia
Proyecto educativo.
