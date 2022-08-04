package com.android.maxclub.nasaapod.data.source.remote

import com.android.maxclub.nasaapod.data.source.remote.api.ApodService
import com.android.maxclub.nasaapod.util.formatDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*
import javax.inject.Inject

class ApodRemoteDataSourceImpl @Inject constructor(
    private val apodService: ApodService
) : ApodRemoteDataSource {
    override fun getApodOfToday(): Flow<ApodDto> = flow {
        emit(apodService.getApodOfToday())
    }.flowOn(Dispatchers.IO)

    override fun getApodByDate(date: Date): Flow<ApodDto> = flow {
        emit(apodService.getApodByDate(formatDate(date, "yyyy-MM-dd", Locale.ENGLISH)))
    }.flowOn(Dispatchers.IO)

    override fun getRandomApod(): Flow<ApodDto> = flow {
        emit(apodService.getRandomApod()[0])
    }.flowOn(Dispatchers.IO)
}