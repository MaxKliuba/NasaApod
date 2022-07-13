package com.android.maxclub.nasaapod.utils

import java.text.SimpleDateFormat
import java.util.*

class DateHelper {
    companion object {
        fun format(pattern: String, date: Date): String {
            val dateFormatter = SimpleDateFormat(pattern, Locale.getDefault())
            return dateFormatter.format(date)
                .replaceFirstChar { it.uppercaseChar() }
        }

        fun parse(pattern: String, date: String): Date? {
            val dateFormatter = SimpleDateFormat(pattern, Locale.getDefault())
            return dateFormatter.parse(date)
        }
    }
}