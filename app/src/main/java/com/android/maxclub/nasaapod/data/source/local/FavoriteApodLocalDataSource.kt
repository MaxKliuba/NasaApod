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

    override suspend fun insertFavoriteApod(favoriteApod: FavoriteApod): Boolean =
        withContext(Dispatchers.IO) {
            favoriteApodDao.insertFavoriteApod(favoriteApod) > 0
        }

    override suspend fun updateFavoriteApod(favoriteApod: FavoriteApod): Boolean =
        withContext(Dispatchers.IO) {
            favoriteApodDao.updateFavoriteApod(favoriteApod) > 0
        }

    override suspend fun deleteFavoriteApod(favoriteApod: FavoriteApod): Boolean =
        withContext(Dispatchers.IO) {
            favoriteApodDao.deleteFavoriteApod(favoriteApod) > 0
        }
}