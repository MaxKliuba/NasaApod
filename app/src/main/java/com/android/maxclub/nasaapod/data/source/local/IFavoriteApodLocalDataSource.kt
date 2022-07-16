package com.android.maxclub.nasaapod.data.source.local

import com.android.maxclub.nasaapod.data.FavoriteApod
import kotlinx.coroutines.flow.Flow
import java.util.*

interface IFavoriteApodLocalDataSource {

    fun getFavoriteApods(): Flow<List<FavoriteApod>>

    fun getFavoriteApodByDate(date: Date): Flow<FavoriteApod?>

    suspend fun insertFavoriteApod(favoriteApod: FavoriteApod): Boolean

    suspend fun updateFavoriteApod(favoriteApod: FavoriteApod): Boolean

    suspend fun deleteFavoriteApod(favoriteApod: FavoriteApod): Boolean
}