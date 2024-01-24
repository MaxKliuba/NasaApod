package com.tech.maxclub.nasaapod.data.local.database

import androidx.room.*
import com.tech.maxclub.nasaapod.data.local.entity.FavoriteApodEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface FavoriteApodDao {
    @Query("SELECT * FROM favorite_apod_table")
    fun getFavoriteApods(): Flow<List<FavoriteApodEntity>>

    @Query("SELECT * FROM favorite_apod_table WHERE date = :date")
    fun getFavoriteApodByDate(date: Date): Flow<FavoriteApodEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavoriteApods(vararg favoriteApods: FavoriteApodEntity)

    @Update
    suspend fun updateFavoriteApods(vararg favoriteApods: FavoriteApodEntity)

    @Delete
    suspend fun deleteFavoriteApods(vararg favoriteApods: FavoriteApodEntity)
}