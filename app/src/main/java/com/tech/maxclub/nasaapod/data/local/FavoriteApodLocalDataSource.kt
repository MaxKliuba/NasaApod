package com.tech.maxclub.nasaapod.data.local

import com.tech.maxclub.nasaapod.data.local.entity.FavoriteApodEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

interface FavoriteApodLocalDataSource {

    fun getFavoriteApods(): Flow<List<FavoriteApodEntity>>

    fun getFavoriteApodByDate(date: Date): Flow<FavoriteApodEntity>

    suspend fun insertFavoriteApods(vararg favoriteApods: FavoriteApodEntity)

    suspend fun updateFavoriteApods(vararg favoriteApods: FavoriteApodEntity)

    suspend fun deleteFavoriteApods(vararg favoriteApods: FavoriteApodEntity)
}