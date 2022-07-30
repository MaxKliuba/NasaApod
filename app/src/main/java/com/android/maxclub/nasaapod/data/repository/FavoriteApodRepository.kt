package com.android.maxclub.nasaapod.data.repository

import com.android.maxclub.nasaapod.data.Apod
import com.android.maxclub.nasaapod.data.FavoriteApod
import com.android.maxclub.nasaapod.data.source.local.IFavoriteApodLocalDataSource
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteApodRepository @Inject constructor(
    private val favoriteApodLocalDataSource: IFavoriteApodLocalDataSource,
) {
    fun getFavoriteApods(): Flow<List<FavoriteApod>> =
        favoriteApodLocalDataSource.getFavoriteApods()

    fun getFavoriteApodByDate(date: Date): Flow<FavoriteApod> =
        favoriteApodLocalDataSource.getFavoriteApodByDate(date)

    suspend fun addApodToFavorites(apod: Apod): Boolean =
        favoriteApodLocalDataSource.insertFavoriteApods(apodToFavoriteApod(apod))

    suspend fun removeApodFromFavorites(apod: Apod): Boolean =
        favoriteApodLocalDataSource.deleteFavoriteApods(apodToFavoriteApod(apod))

    suspend fun updateFavoriteApods(vararg favoriteApods: FavoriteApod) =
        favoriteApodLocalDataSource.updateFavoriteApods(*favoriteApods)

    private fun apodToFavoriteApod(apod: Apod): FavoriteApod =
        FavoriteApod(
            date = apod.date,
            title = apod.title,
            mediaType = apod.mediaType,
            url = apod.url,
            copyright = apod.copyright,
        )
}