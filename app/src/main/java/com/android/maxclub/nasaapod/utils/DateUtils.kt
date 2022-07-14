package com.android.maxclub.nasaapod.utils

import java.text.SimpleDateFormat
import java.util.*

fun formatDate(
    date: Date,
    pattern: String,
    locale: Locale = Locale.getDefault()
): String {
    val dateFormatter = SimpleDateFormat(pattern, locale)
    return dateFormatter.format(date)
        .replaceFirstChar { it.uppercaseChar() }
}

fun parseDate(
    date: String,
    pattern: String,
    locale: Locale = Locale.getDefault()
): Date? {
    val dateFormatter = SimpleDateFormat(pattern, locale)
    return dateFormatter.parse(date)
}