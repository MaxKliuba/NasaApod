package com.android.maxclub.nasaapod.data.source.remote

import com.android.maxclub.nasaapod.api.ApodService
import com.android.maxclub.nasaapod.data.Apod
import com.android.maxclub.nasaapod.utils.formatDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*
import javax.inject.Inject

class ApodRemoteDataSource @Inject constructor(
    private val apodService: ApodService
) : IApodRemoteDataSource {
    override fun getApodOfToday(): Flow<Apod> = flow {
        emit(apodService.getApodOfToday())
    }.flowOn(Dispatchers.IO)

    override fun getApodByDate(date: Date): Flow<Apod> = flow {
        emit(apodService.getApodByDate(formatDate(date, "yyyy-MM-dd", Locale.ENGLISH)))
    }.flowOn(Dispatchers.IO)

    override fun getRandomApod(): Flow<Apod> = flow {
        emit(apodService.getRandomApod()[0])
    }.flowOn(Dispatchers.IO)
}