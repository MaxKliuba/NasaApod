package com.android.maxclub.nasaapod.data.repository

import com.android.maxclub.nasaapod.data.Apod
import com.android.maxclub.nasaapod.data.FavoriteApod
import com.android.maxclub.nasaapod.data.source.local.FavoriteApodLocalDataSource
import com.android.maxclub.nasaapod.data.source.remote.ApodDto
import com.android.maxclub.nasaapod.data.source.remote.ApodRemoteDataSource
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApodRepository @Inject constructor(
    private val apodRemoteDataSource: ApodRemoteDataSource,
    private val favoriteApodLocalDataSource: FavoriteApodLocalDataSource,
) {
    fun getApodOfToday(): Flow<Apod> =
        apodRemoteDataSource.getApodOfToday()
            .toApodFlow()

    fun getApodByDate(date: Date): Flow<Apod> =
        apodRemoteDataSource.getApodByDate(date)
            .toApodFlow()

    fun getRandomApod(): Flow<Apod> =
        apodRemoteDataSource.getRandomApod()
            .toApodFlow()

    private fun getFavoriteApodByDate(date: Date): Flow<FavoriteApod> =
        favoriteApodLocalDataSource.getFavoriteApodByDate(date)

    private fun Flow<ApodDto>.toApodFlow(): Flow<Apod> =
        map { apodDto ->
            Apod(
                date = apodDto.date,
                title = apodDto.title,
                explanation = apodDto.explanation,
                mediaType = apodDto.mediaType,
                url = apodDto.url,
                hdUrl = apodDto.hdUrl,
                copyright = apodDto.copyright,
                isFavorite = getFavoriteApodByDate(apodDto.date).firstOrNull() != null,
            )
        }
}