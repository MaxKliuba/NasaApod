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
        favoriteApodLocalDataSource.insertFavoriteApods(apod.toFavoriteApod())

    suspend fun removeApodFromFavorites(apod: Apod): Boolean =
        favoriteApodLocalDataSource.deleteFavoriteApods(apod.toFavoriteApod())

    suspend fun removeFavoriteApod(favoriteApod: FavoriteApod): Boolean =
        favoriteApodLocalDataSource.deleteFavoriteApods(favoriteApod)

    suspend fun updateFavoriteApods(vararg favoriteApods: FavoriteApod) =
        favoriteApodLocalDataSource.updateFavoriteApods(*favoriteApods)
}