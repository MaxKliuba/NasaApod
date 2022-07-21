package com.android.maxclub.nasaapod.data

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
        favoriteApodLocalDataSource.insertFavoriteApod(apodToFavoriteApod(apod))

    suspend fun removeApodFromFavorites(apod: Apod): Boolean =
        favoriteApodLocalDataSource.deleteFavoriteApod(apodToFavoriteApod(apod))

    suspend fun updateFavoriteApod(favoriteApod: FavoriteApod) =
        favoriteApodLocalDataSource.updateFavoriteApod(favoriteApod)

    private fun apodToFavoriteApod(apod: Apod): FavoriteApod =
        FavoriteApod(
            url = apod.url,
            title = apod.title,
            date = apod.date,
            copyright = apod.copyright,
        )
}