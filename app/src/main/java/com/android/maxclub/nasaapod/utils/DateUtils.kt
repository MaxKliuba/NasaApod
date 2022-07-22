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

private val calendar: Calendar = Calendar.getInstance()

fun getNextDate(date: Date): Date =
    calendar.let { calendar ->
        calendar.time = date
        calendar.add(Calendar.DATE, 1)
        calendar.time
    }

fun getPrevDate(date: Date): Date =
    calendar.let { calendar ->
        calendar.time = date
        calendar.add(Calendar.DATE, -1)
        calendar.time
    }