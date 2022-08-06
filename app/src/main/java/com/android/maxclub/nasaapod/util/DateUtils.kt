package com.android.maxclub.nasaapod.util

import java.text.SimpleDateFormat
import java.util.*

fun formatDate(
    date: Date,
    pattern: String = DEFAULT_DATE_PATTERN,
    locale: Locale = DEFAULT_DATE_LOCALE,
): String {
    val dateFormatter = SimpleDateFormat(pattern, locale).apply {
        timeZone = UTC_TIME_ZONE
    }
    return dateFormatter.format(date).replaceFirstChar { it.uppercaseChar() }
}

fun parseDate(
    date: String,
    pattern: String = DEFAULT_DATE_PATTERN,
    locale: Locale = DEFAULT_DATE_LOCALE,
): Date? {
    val dateFormatter = SimpleDateFormat(pattern, locale).apply {
        timeZone = UTC_TIME_ZONE
    }
    return dateFormatter.parse(date)
}