package com.android.maxclub.nasaapod.domain.usecase

import com.android.maxclub.nasaapod.domain.util.InvalidServiceDateException
import com.android.maxclub.nasaapod.domain.util.ServiceDate
import com.android.maxclub.nasaapod.util.UTC_TIME_ZONE
import java.util.*
import javax.inject.Inject
import kotlin.jvm.Throws

class GetServiceDateUseCase @Inject constructor() {

    @Throws(InvalidServiceDateException::class)
    operator fun invoke(serviceDate: ServiceDate): Date =
        when (serviceDate) {
            is ServiceDate.Start -> Date(START_DATE)
            is ServiceDate.Today -> getTodayDate()
            is ServiceDate.NextTo -> getNextDateTo(serviceDate.date)
                ?: throw InvalidServiceDateException("The next date was not found")
            is ServiceDate.PreviousTo -> getPreviousDateTo(serviceDate.date)
                ?: throw InvalidServiceDateException("The previous date was not found")
        }

    fun contains(date: Date): Boolean =
        (date.time <= getTodayDate().time && date.time >= FROM_DATE) || date.time == START_DATE

    private fun getTodayDate(): Date =
        Calendar.getInstance(UTC_TIME_ZONE).apply {
            timeInMillis =
                Date().time + TIME_ZONE.rawOffset + TIME_ZONE.dstSavings
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

    private fun getNextDateTo(date: Date): Date? =
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

    private fun getPreviousDateTo(date: Date): Date? =
        Calendar.getInstance(UTC_TIME_ZONE).let { calendar ->
            calendar.time = date
            calendar.add(Calendar.DATE, -1)
            calendar.time.let { prevDate ->
                when {
                    prevDate.time >= FROM_DATE -> prevDate
                    prevDate.time > START_DATE -> Date(START_DATE)
                    else -> null
                }
            }
        }

    companion object {
        private const val START_DATE: Long = 803260800000L
        private const val FROM_DATE: Long = 803606400000L

        private val TIME_ZONE: TimeZone = TimeZone.getTimeZone("America/New_York")
    }
}