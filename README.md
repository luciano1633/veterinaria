# Sistema de Gestión Veterinaria

## Descripción
Este proyecto es una aplicación de consola desarrollada en Kotlin para gestionar consultas veterinarias. Permite registrar mascotas, dueños, calcular costos con descuentos, verificar disponibilidad de veterinarios y agendar consultas.

## Características
- Registro de mascotas con validaciones (nombre, especie, edad, peso).
- Registro de dueños con validaciones (nombre, teléfono, email).
- Cálculo de costos con descuento del 15% para múltiples mascotas.
- Verificación de disponibilidad de veterinarios (lunes a sábado, 08:00 - 16:00).
- Agenda de consultas con prevención de conflictos de horario.
- Resumen detallado de la consulta agendada.

## Requisitos
- JDK 21 o superior.
- IntelliJ IDEA o cualquier IDE compatible con Kotlin.
- Kotlin 2.2.20.

## Instalación
1. Clona el repositorio:
   ```
   git clone <url-del-repositorio>
   ```
2. Abre el proyecto en IntelliJ IDEA.
3. Asegúrate de que las dependencias de Kotlin estén configuradas.

## Estructura del Proyecto
```
veterinaria/
├── src/
│   ├── Main.kt          # Archivo principal con la lógica del programa
│   └── model/
│       ├── Mascota.kt   # Clase para representar mascotas
│       ├── Dueño.kt     # Clase para representar dueños
│       └── Consulta.kt  # Clase para representar consultas
├── out/                 # Archivos compilados (generados)
└── veterinaria.iml      # Archivo de configuración de IntelliJ
```

## Uso
1. Ejecuta el programa desde IntelliJ IDEA o mediante la línea de comandos:
   ```
   kotlin Main.kt
   ```
2. Sigue las instrucciones en pantalla para ingresar datos.
3. El programa validará todas las entradas y mostrará un resumen al final.

## Validaciones Implementadas
- Número de mascotas: Entero positivo.
- Nombre de mascota: Solo letras y espacios.
- Especie: "perro" o "gato".
- Edad: Número entero positivo.
- Peso: Número positivo.
- Nombre del dueño: Solo letras y espacios.
- Teléfono: Exactamente 8 dígitos numéricos.
- Email: Formato válido de email.
- Fecha: Formato YYYY-MM-DD, fecha válida.
- Hora: Formato HH:MM, hora válida.
- Respuesta s/n: Solo "s" o "n".

## Horario de Veterinarios
- Días disponibles: Lunes a Sábado.
- Horario: 08:00 a 16:00.
- No disponible los domingos ni fuera del horario.

## Descuentos
- Si se atienden 2 o más mascotas, se aplica un descuento del 15% sobre el costo base (5000.0).

## Contribución
Si deseas contribuir, por favor crea un pull request con tus cambios.

## Licencia
Este proyecto es de uso educativo y no tiene licencia específica.</content>
<parameter name="filePath">c:\Proyectos Duoc\veterinaria\README.md
