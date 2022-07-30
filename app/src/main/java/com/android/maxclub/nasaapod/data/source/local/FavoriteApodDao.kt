package com.android.maxclub.nasaapod.data.source.local

import androidx.room.*
import com.android.maxclub.nasaapod.data.FavoriteApod
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface FavoriteApodDao {
    @Query("SELECT * FROM favorite_apod_table")
    fun getFavoriteApods(): Flow<List<FavoriteApod>>

    @Query("SELECT * FROM favorite_apod_table WHERE date = :date")
    fun getFavoriteApodByDate(date: Date): Flow<FavoriteApod>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavoriteApods(vararg favoriteApods: FavoriteApod): LongArray

    @Update
    suspend fun updateFavoriteApods(vararg favoriteApods: FavoriteApod): Int

    @Delete
    suspend fun deleteFavoriteApods(vararg favoriteApods: FavoriteApod): Int
}