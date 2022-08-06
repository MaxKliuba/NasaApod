package com.android.maxclub.nasaapod.util

import java.util.*

class ServiceDateManager {
    companion object {
        const val FIRST_DATE: Long = 803260800000L
        const val FROM_DATE: Long = 803606400000L

        private val TIME_ZONE: TimeZone = TimeZone.getTimeZone("America/New_York")

        fun isDateValid(date: Date): Boolean =
            (date.time <= getTodayDate().time && date.time >= FROM_DATE) || date.time == FIRST_DATE

        fun getTodayDate(): Date =
            Calendar.getInstance(UTC_TIME_ZONE).apply {
                timeInMillis =
                    Date().time + TIME_ZONE.rawOffset + TIME_ZONE.dstSavings
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

        fun getNextDate(date: Date): Date? =
            Calendar.getInstance(UTC_TIME_ZONE).let { calendar ->
                calendar.time = date
                calendar.add(Calendar.DATE, 1)
                calendar.time.let { nextDate ->
                    when {
                        nextDate.time < FROM_DATE -> Date(FROM_DATE)
                        nextDate <= getTodayDate() -> nextDate
                        else -> null
                    }
                }
            }

        fun getPrevDate(date: Date): Date? =
            Calendar.getInstance(UTC_TIME_ZONE).let { calendar ->
                calendar.time = date
                calendar.add(Calendar.DATE, -1)
                calendar.time.let { prevDate ->
                    when {
                        prevDate.time >= FROM_DATE -> prevDate
                        prevDate.time > FIRST_DATE -> Date(FIRST_DATE)
                        else -> null
                    }
                }
            }
    }
}