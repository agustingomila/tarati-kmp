package com.agustin.tarati.core.utils.helpers

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

/**
 * Retorna la fecha actual en formato "yyyy.MM.dd".
 * Ejemplo: "2026.05.11"
 */
fun getCurrentDate(): String {
    val now = Clock.System.now()
    val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())

    val year = localDateTime.year
    val month = localDateTime.month.number.toString().padStart(2, '0')
    val day = localDateTime.day.toString().padStart(2, '0')

    return "$year.$month.$day"
}

/**
 * Retorna la hora actual como Float (0.0 - 23.99).
 * Ejemplo: 14:30 → 14.5
 */
fun getCurrentHour(): Float {
    val now = Clock.System.now()
    val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())

    return localDateTime.hour.toFloat() + localDateTime.minute.toFloat() / 60f
}