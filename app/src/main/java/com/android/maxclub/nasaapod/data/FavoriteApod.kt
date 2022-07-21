package com.android.maxclub.nasaapod.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "favorite_apod_table")
data class FavoriteApod(
    @ColumnInfo(name = "url")
    val url: String? = null,

    @ColumnInfo(name = "title")
    val title: String? = null,

    @ColumnInfo(name = "date")
    @PrimaryKey val date: Date,

    @ColumnInfo(name = "copyright")
    val copyright: String? = null,

    @ColumnInfo(name = "position")
    val position: Int = -1,

    @ColumnInfo(name = "is_new")
    val isNew: Boolean = true,
)
