package com.android.maxclub.nasaapod.data.repository

import com.android.maxclub.nasaapod.data.FavoriteApod
import com.android.maxclub.nasaapod.data.source.local.FavoriteApodLocalDataSource
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteApodRepository @Inject constructor(
    private val favoriteApodLocalDataSource: FavoriteApodLocalDataSource,
) {
    fun getFavoriteApods(): Flow<List<FavoriteApod>> =
        favoriteApodLocalDataSource.getFavoriteApods()

    suspend fun addFavoriteApod(favoriteApod: FavoriteApod): Boolean =
        favoriteApodLocalDataSource.insertFavoriteApods(favoriteApod)

    suspend fun removeFavoriteApod(favoriteApod: FavoriteApod): Boolean =
        favoriteApodLocalDataSource.deleteFavoriteApods(favoriteApod)

    suspend fun updateFavoriteApods(vararg favoriteApods: FavoriteApod) =
        favoriteApodLocalDataSource.updateFavoriteApods(*favoriteApods)
}