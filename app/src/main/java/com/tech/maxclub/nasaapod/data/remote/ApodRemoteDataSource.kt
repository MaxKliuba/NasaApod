package com.tech.maxclub.nasaapod.data.remote

import com.tech.maxclub.nasaapod.data.remote.dto.ApodDto
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ApodRemoteDataSource {

    fun getApodOfToday(): Flow<ApodDto>

    fun getApodByDate(date: Date): Flow<ApodDto>

    fun getRandomApod(): Flow<ApodDto>
}