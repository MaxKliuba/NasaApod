package com.android.maxclub.nasaapod.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApodDateRepository @Inject constructor() {
    private val apodDates = MutableStateFlow<List<ApodDate>>(listOf(ApodDate.Today()))

    fun getApodDates(): Flow<List<ApodDate>> = apodDates

    fun addNewDate(newDate: ApodDate) {
        apodDates.value += newDate
    }

    fun replaceAllWithNewDate(newDate: ApodDate) {
        apodDates.value = listOf(newDate)
    }

    fun updateApodDate(apodDate: ApodDate) {
        apodDates.value = apodDates.value.map {
            if (it.id == apodDate.id) {
                apodDate
            } else {
                it
            }
        }
    }
}