package xyz.teamgravity.coresdkandroid.time

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Converts current LocalDate to LocalDateTime that has maximum hours for the day.
 */
fun LocalDate.atEndOfDay(): LocalDateTime = LocalDateTime.of(this, LocalTime.MAX)

/**
 * Returns true if date is equal to today. Otherwise, returns false.
 */
val LocalDate.isToday: Boolean
    get() = isEqual(LocalDate.now())

/**
 * Returns true if date is equal to yesterday. Otherwise, returns false.
 */
val LocalDate.isYesterday: Boolean
    get() = isEqual(LocalDate.now().minusDays(1))