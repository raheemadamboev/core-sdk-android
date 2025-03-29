package xyz.teamgravity.coresdkandroid.time

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

object TimeUtil {

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Converts [millis] to LocalDate.
     *
     * @param millis
     * Epoch millis to convert to LocalDate.
     */
    fun fromLongToLocalDate(millis: Long): LocalDate {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    /**
     * Converts [millis] to LocalDateTime.
     *
     * @param millis
     * Epoch millis to convert to LocalDateTime.
     */
    fun fromLongToLocalDateTime(millis: Long): LocalDateTime {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

    /**
     * Converts passed [date] LocalDate to long epoch millis.
     *
     * @param date
     * LocalDate to convert to epoch milliseconds.
     */
    fun fromLocalDateToLong(date: LocalDate): Long {
        return fromLocalDateTimeToLong(date.atStartOfDay())
    }

    /**
     * Converts passed [date] LocalDateTime to long epoch millis.
     *
     * @param date
     * LocalDateTime to convert to epoch milliseconds.
     */
    fun fromLocalDateTimeToLong(date: LocalDateTime): Long {
        return date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}