package main.util

import java.time.LocalDate
import java.time.LocalTime

object Parsers {
    fun fecha(s: String) = runCatching { LocalDate.parse(s) }
    fun hora(s: String) = runCatching { LocalTime.parse(s) }
}
