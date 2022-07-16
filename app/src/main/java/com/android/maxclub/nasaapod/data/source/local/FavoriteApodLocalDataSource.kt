package com.android.maxclub.nasaapod.data.source.local

import com.android.maxclub.nasaapod.data.FavoriteApod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.*

class FavoriteApodLocalDataSource : IFavoriteApodLocalDataSource {
    private val favoriteApods: MutableList<FavoriteApod> = mutableListOf()

    override fun getFavoriteApods(): Flow<List<FavoriteApod>> = flow {
        emit(favoriteApods)
    }.flowOn(Dispatchers.IO)

    override fun getFavoriteApodByDate(date: Date): Flow<FavoriteApod?> = flow {
        val favoriteApod = favoriteApods.firstOrNull { it.date == date }
        emit(favoriteApod)
    }.flowOn(Dispatchers.IO)

    override suspend fun insertFavoriteApod(favoriteApod: FavoriteApod): Boolean =
        withContext(Dispatchers.IO) {
            favoriteApods += favoriteApod
            true
        }

    override suspend fun updateFavoriteApod(favoriteApod: FavoriteApod): Boolean =
        withContext(Dispatchers.IO) {
            // TODO
            false
        }

    override suspend fun deleteFavoriteApod(favoriteApod: FavoriteApod): Boolean =
        withContext(Dispatchers.IO) {
            favoriteApods -= favoriteApod
            true
        }
}