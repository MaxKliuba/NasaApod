package com.tech.maxclub.nasaapod.data.local

import com.tech.maxclub.nasaapod.data.local.database.FavoriteApodDao
import com.tech.maxclub.nasaapod.data.local.entity.FavoriteApodEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class FavoriteApodLocalDataSourceImpl @Inject constructor(
    private val favoriteApodDao: FavoriteApodDao
) : FavoriteApodLocalDataSource {

    override fun getFavoriteApods(): Flow<List<FavoriteApodEntity>> =
        favoriteApodDao.getFavoriteApods()

    override fun getFavoriteApodByDate(date: Date): Flow<FavoriteApodEntity> =
        favoriteApodDao.getFavoriteApodByDate(date)

    override suspend fun insertFavoriteApods(vararg favoriteApods: FavoriteApodEntity) {
        favoriteApodDao.insertFavoriteApods(*favoriteApods)
    }

    override suspend fun updateFavoriteApods(vararg favoriteApods: FavoriteApodEntity) {
        favoriteApodDao.updateFavoriteApods(*favoriteApods)
    }

    override suspend fun deleteFavoriteApods(vararg favoriteApods: FavoriteApodEntity) {
        favoriteApodDao.deleteFavoriteApods(*favoriteApods)
    }
}