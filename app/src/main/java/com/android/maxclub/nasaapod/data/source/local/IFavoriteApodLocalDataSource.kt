package com.android.maxclub.nasaapod.data.source.local

import com.android.maxclub.nasaapod.data.FavoriteApod
import kotlinx.coroutines.flow.Flow
import java.util.*

interface IFavoriteApodLocalDataSource {

    fun getFavoriteApods(): Flow<List<FavoriteApod>>

    fun getFavoriteApodByDate(date: Date): Flow<FavoriteApod>

    suspend fun insertFavoriteApods(vararg favoriteApods: FavoriteApod): Boolean

    suspend fun updateFavoriteApods(vararg favoriteApods: FavoriteApod): Boolean

    suspend fun deleteFavoriteApods(vararg favoriteApods: FavoriteApod): Boolean
}