package com.android.maxclub.nasaapod.data.source.remote

import com.android.maxclub.nasaapod.data.Apod
import kotlinx.coroutines.flow.Flow
import java.util.*

interface IApodRemoteDataSource {

    fun getApodOfToday(): Flow<Apod>

    fun getApodByDate(date: Date): Flow<Apod>

    fun getRandomApod(): Flow<Apod>
}