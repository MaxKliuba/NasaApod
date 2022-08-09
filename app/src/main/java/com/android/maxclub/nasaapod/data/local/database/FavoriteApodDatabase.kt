package com.android.maxclub.nasaapod.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.android.maxclub.nasaapod.data.local.entity.FavoriteApodEntity

@Database(entities = [FavoriteApodEntity::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class FavoriteApodDatabase : RoomDatabase() {
    abstract fun favoriteApodDao(): FavoriteApodDao

    companion object {
        @Volatile
        private var INSTANCE: FavoriteApodDatabase? = null

        fun create(context: Context): FavoriteApodDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteApodDatabase::class.java,
                    "favorite_apod_db"
                )
                    .addCallback(databaseCallback)
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }

        private val databaseCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL(
                    """
                        CREATE TRIGGER set_position_value_trigger AFTER INSERT ON favorite_apod_table
                        BEGIN
                            UPDATE favorite_apod_table 
                            SET `position` = (SELECT MAX(`position`) + 1 FROM favorite_apod_table) 
                            WHERE `date` = NEW.date AND `position` = -1;
                        END;
                    """.trimIndent()
                )
            }
        }
    }
}