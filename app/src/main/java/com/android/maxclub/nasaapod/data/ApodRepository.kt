package com.android.maxclub.nasaapod.data

import com.android.maxclub.nasaapod.api.ApodService
import com.android.maxclub.nasaapod.utils.formatDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class ApodRepository @Inject constructor(
    private val apodService: ApodService
) {
    suspend fun getApod() = withContext(Dispatchers.IO) {
        apodService.getApod()
    }

    suspend fun getApodByDate(date: Date) = withContext(Dispatchers.IO) {
        apodService.getApodByDate(formatDate(date, "yyyy-MM-dd", Locale.ENGLISH))
    }

    suspend fun getRandomApod(count: Int = 1) = withContext(Dispatchers.IO) {
        apodService.getRandomApod(count)
    }
}