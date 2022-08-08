package com.android.maxclub.nasaapod.data.local

import com.android.maxclub.nasaapod.data.local.database.FavoriteApodDao
import com.android.maxclub.nasaapod.data.local.entity.FavoriteApodEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class FavoriteApodLocalDataSourceImpl @Inject constructor(
    private val favoriteApodDao: FavoriteApodDao
) : FavoriteApodLocalDataSource {

    override fun getFavoriteApods(): Flow<List<FavoriteApodEntity>> =
        favoriteApodDao.getFavoriteApods()
            .flowOn(Dispatchers.IO)

    override fun getFavoriteApodByDate(date: Date): Flow<FavoriteApodEntity> =
        favoriteApodDao.getFavoriteApodByDate(date)
            .flowOn(Dispatchers.IO)

    override suspend fun insertFavoriteApods(vararg favoriteApods: FavoriteApodEntity) =
        withContext(Dispatchers.IO) {
            favoriteApodDao.insertFavoriteApods(*favoriteApods)
        }

    override suspend fun updateFavoriteApods(vararg favoriteApods: FavoriteApodEntity) =
        withContext(Dispatchers.IO) {
            favoriteApodDao.updateFavoriteApods(*favoriteApods)
        }

    override suspend fun deleteFavoriteApods(vararg favoriteApods: FavoriteApodEntity) =
        withContext(Dispatchers.IO) {
            favoriteApodDao.deleteFavoriteApods(*favoriteApods)
        }
}