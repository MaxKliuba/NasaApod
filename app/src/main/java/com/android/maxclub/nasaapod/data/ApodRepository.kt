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


    fun getFavoriteApods(): Flow<List<FavoriteApod>> =
        favoriteApodLocalDataSource.getFavoriteApods()

    private fun getFavoriteApodByDate(date: Date): Flow<FavoriteApod?> =
        favoriteApodLocalDataSource.getFavoriteApodByDate(date)

    fun addApodToFavorites(apod: Apod): Flow<Apod> = flow {
        emit(
            favoriteApodLocalDataSource.insertFavoriteApod(
                FavoriteApod(
                    url = apod.url,
                    title = apod.title,
                    date = apod.date,
                    copyright = apod.copyright,
                )
            ).let { isSuccess ->
                apod.apply {
                    if (isSuccess) isFavorite = true
                }
            }
        )
    }

    fun removeApodFromFavorites(apod: Apod): Flow<Apod> = flow {
        emit(
            favoriteApodLocalDataSource.deleteFavoriteApod(
                FavoriteApod(
                    url = apod.url,
                    title = apod.title,
                    date = apod.date,
                    copyright = apod.copyright,
                )
            ).let { isSuccess ->
                apod.apply {
                    if (isSuccess) isFavorite = false
                }
            }
        )
    }

    suspend fun updateFavoriteApod(favoriteApod: FavoriteApod) =
        favoriteApodLocalDataSource.updateFavoriteApod(favoriteApod)

    private fun Flow<Apod>.checkIsFavorite(): Flow<Apod> =
        onEach { apod ->
            getFavoriteApodByDate(apod.date).first()?.let {
                apod.isFavorite = true
            }
        }
}