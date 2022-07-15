package com.android.maxclub.nasaapod.data

import com.android.maxclub.nasaapod.data.source.remote.IApodRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApodRepository @Inject constructor(
    private val apodRemoteDataSource: IApodRemoteDataSource
) {
    fun getApodOfToday(): Flow<Apod> =
        apodRemoteDataSource.getApodOfToday()
            .onEach {

            }

    fun getApodByDate(date: Date): Flow<Apod> =
        apodRemoteDataSource.getApodByDate(date)

    fun getRandomApod(): Flow<Apod> =
        apodRemoteDataSource.getRandomApod()
}