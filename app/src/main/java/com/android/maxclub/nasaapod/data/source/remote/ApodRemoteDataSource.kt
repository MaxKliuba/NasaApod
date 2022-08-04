package com.android.maxclub.nasaapod.data.source.remote

import kotlinx.coroutines.flow.Flow
import java.util.*

interface ApodRemoteDataSource {

    fun getApodOfToday(): Flow<ApodDto>

    fun getApodByDate(date: Date): Flow<ApodDto>

    fun getRandomApod(): Flow<ApodDto>
}