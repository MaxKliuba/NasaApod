package com.android.maxclub.nasaapod.repository

import com.android.maxclub.nasaapod.api.ApodService
import com.android.maxclub.nasaapod.utils.DateHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class ApodRepository(private val apodService: ApodService) {
    suspend fun getApod() = withContext(Dispatchers.IO) {
        apodService.getApod()
    }

    suspend fun getApodByDate(date: Date) = withContext(Dispatchers.IO) {
        apodService.getApodByDate(DateHelper.format("yyyy-MM-dd", date))
    }

    suspend fun getRandomApod(count: Int = 1) = withContext(Dispatchers.IO) {
        apodService.getRandomApod(count)
    }
}