package com.android.maxclub.nasaapod.data.repository

import com.android.maxclub.nasaapod.data.local.FavoriteApodLocalDataSource
import com.android.maxclub.nasaapod.domain.util.toFavoriteApod
import com.android.maxclub.nasaapod.domain.model.FavoriteApod
import com.android.maxclub.nasaapod.domain.repository.FavoriteApodRepository
import com.android.maxclub.nasaapod.domain.util.toFavoriteApodEntity
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class FavoriteApodRepositoryImpl @Inject constructor(
    private val favoriteApodLocalDataSource: FavoriteApodLocalDataSource,
) : FavoriteApodRepository {

    override fun getFavoriteApods(): Flow<List<FavoriteApod>> =
        favoriteApodLocalDataSource.getFavoriteApods()
            .map { favoriteApodEntities ->
                favoriteApodEntities.map { favoriteApodEntity ->
                    favoriteApodEntity.toFavoriteApod()
                }
            }

    override suspend fun addFavoriteApod(favoriteApod: FavoriteApod) =
        favoriteApodLocalDataSource.insertFavoriteApods(favoriteApod.toFavoriteApodEntity())

    override suspend fun deleteFavoriteApod(favoriteApod: FavoriteApod) =
        favoriteApodLocalDataSource.deleteFavoriteApods(favoriteApod.toFavoriteApodEntity())

    override suspend fun updateFavoriteApods(vararg favoriteApods: FavoriteApod) =
        favoriteApodLocalDataSource.updateFavoriteApods(
            *favoriteApods.map { favoriteApod ->
                favoriteApod.toFavoriteApodEntity()
            }.toTypedArray()
        )
}