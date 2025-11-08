package com.eliaskrr.fitmacros.ui.profile

import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.Period
import java.time.ZoneId

internal fun String.toMillis(formatter: SimpleDateFormat): Long? {
    if (isBlank()) return null
    return try {
        formatter.parse(this)?.time
    } catch (_: ParseException) {
        null
    }
}

internal fun ageInYears(birthMillis: Long, nowMillis: Long = System.currentTimeMillis()): Int? {
    val birthDate = Instant.ofEpochMilli(birthMillis).atZone(ZoneId.systemDefault()).toLocalDate()
    val today = Instant.ofEpochMilli(nowMillis).atZone(ZoneId.systemDefault()).toLocalDate()
    if (birthDate.isAfter(today)) return null
    return Period.between(birthDate, today).years
}

internal fun isAgeAllowed(birthMillis: Long, nowMillis: Long = System.currentTimeMillis()): Boolean {
    val age = ageInYears(birthMillis, nowMillis) ?: return false
    return age in 0..100
}
