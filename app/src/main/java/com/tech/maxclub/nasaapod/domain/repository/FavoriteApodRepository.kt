package com.tech.maxclub.nasaapod.domain.repository

import com.tech.maxclub.nasaapod.domain.model.FavoriteApod
import kotlinx.coroutines.flow.Flow

interface FavoriteApodRepository {

    fun getFavoriteApods(): Flow<List<FavoriteApod>>

    suspend fun addFavoriteApod(favoriteApod: FavoriteApod)

    suspend fun deleteFavoriteApod(favoriteApod: FavoriteApod)

    suspend fun updateFavoriteApods(vararg favoriteApods: FavoriteApod)
}