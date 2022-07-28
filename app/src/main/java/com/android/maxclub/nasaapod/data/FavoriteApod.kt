package com.android.maxclub.nasaapod.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(tableName = "favorite_apod_table")
data class FavoriteApod(
    @ColumnInfo(name = "date")
    @PrimaryKey val date: Date,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "media_type")
    val mediaType: MediaType,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "copyright")
    val copyright: String? = null,

    @ColumnInfo(name = "position")
    val position: Int = -1,

    @ColumnInfo(name = "is_new")
    val isNew: Boolean = true,
) : Serializable