package com.android.maxclub.nasaapod.data

import com.android.maxclub.nasaapod.data.source.local.IFavoriteApodLocalDataSource
import com.android.maxclub.nasaapod.data.source.remote.IApodRemoteDataSource
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApodRepository @Inject constructor(
    private val apodRemoteDataSource: IApodRemoteDataSource,
    private val favoriteApodLocalDataSource: IFavoriteApodLocalDataSource,
) {
    fun getApodOfToday(): Flow<Apod> =
        apodRemoteDataSource.getApodOfToday()
            .checkIsFavorite()

    fun getApodByDate(date: Date): Flow<Apod> =
        apodRemoteDataSource.getApodByDate(date)
            .checkIsFavorite()

    fun getRandomApod(): Flow<Apod> =
        apodRemoteDataSource.getRandomApod()
            .checkIsFavorite()

    private fun getFavoriteApodByDate(date: Date): Flow<FavoriteApod> =
        favoriteApodLocalDataSource.getFavoriteApodByDate(date)

    private fun Flow<Apod>.checkIsFavorite(): Flow<Apod> =
        map { apod ->
            getFavoriteApodByDate(apod.date)
                .firstOrNull()?.let {
                    apod.copy(isFavorite = true)
                } ?: apod
        }
}