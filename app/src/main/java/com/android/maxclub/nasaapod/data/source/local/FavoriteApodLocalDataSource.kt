package com.android.maxclub.nasaapod.data.source.local

import com.android.maxclub.nasaapod.data.FavoriteApod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class FavoriteApodLocalDataSource @Inject constructor(
    private val favoriteApodDao: FavoriteApodDao
) : IFavoriteApodLocalDataSource {
    override fun getFavoriteApods(): Flow<List<FavoriteApod>> =
        favoriteApodDao.getFavoriteApods()
            .flowOn(Dispatchers.IO)

    override fun getFavoriteApodByDate(date: Date): Flow<FavoriteApod> =
        favoriteApodDao.getFavoriteApodByDate(date)
            .flowOn(Dispatchers.IO)

    override suspend fun insertFavoriteApods(vararg favoriteApods: FavoriteApod): Boolean =
        withContext(Dispatchers.IO) {
            favoriteApodDao.insertFavoriteApods(*favoriteApods).isNotEmpty()
        }

    override suspend fun updateFavoriteApods(vararg favoriteApods: FavoriteApod): Boolean =
        withContext(Dispatchers.IO) {
            favoriteApodDao.updateFavoriteApods(*favoriteApods) > 0
        }

    override suspend fun deleteFavoriteApods(vararg favoriteApods: FavoriteApod): Boolean =
        withContext(Dispatchers.IO) {
            favoriteApodDao.deleteFavoriteApods(*favoriteApods) > 0
        }
}