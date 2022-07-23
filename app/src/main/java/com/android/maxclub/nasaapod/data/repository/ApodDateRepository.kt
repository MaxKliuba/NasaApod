package com.android.maxclub.nasaapod.data.repository

import com.android.maxclub.nasaapod.data.ApodDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApodDateRepository @Inject constructor() {
    private val apodDates = MutableStateFlow<Set<ApodDate>>(setOf(ApodDate.Today()))
    private var lastLoadedDate = MutableStateFlow<Date?>(null)

    fun getApodDates(): Flow<Set<ApodDate>> = apodDates

    fun addNewApodDate(newDate: ApodDate) {
        apodDates.value += newDate
    }

    fun replaceAllWithNewApodDate(newDate: ApodDate) {
        apodDates.value = setOf(newDate)
        updateLastLoadedDate(null)
    }

    fun updateApodDate(apodDate: ApodDate) {
        apodDates.value = apodDates.value.map {
            if (it.id == apodDate.id) {
                apodDate
            } else {
                it
            }
        }.toSet()
    }

    fun getLastLoadedDate(): Flow<Date?> = lastLoadedDate

    fun updateLastLoadedDate(newDate: Date?) {
        lastLoadedDate.value = newDate
    }
}