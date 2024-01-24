package com.tech.maxclub.nasaapod.data.remote

import com.tech.maxclub.nasaapod.data.remote.api.ApodService
import com.tech.maxclub.nasaapod.data.remote.dto.ApodDto
import com.tech.maxclub.nasaapod.util.DEFAULT_DATE_LOCALE
import com.tech.maxclub.nasaapod.util.DEFAULT_DATE_PATTERN
import com.tech.maxclub.nasaapod.util.formatDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date
import javax.inject.Inject

class ApodRemoteDataSourceImpl @Inject constructor(
    private val apodService: ApodService
) : ApodRemoteDataSource {

    override fun getApodOfToday(): Flow<ApodDto> = flow {
        emit(apodService.getApodOfToday())
    }

    override fun getApodByDate(date: Date): Flow<ApodDto> = flow {
        emit(apodService.getApodByDate(formatDate(date, DEFAULT_DATE_PATTERN, DEFAULT_DATE_LOCALE)))
    }

    override fun getRandomApod(): Flow<ApodDto> = flow {
        emit(apodService.getRandomApod()[0])
    }
}