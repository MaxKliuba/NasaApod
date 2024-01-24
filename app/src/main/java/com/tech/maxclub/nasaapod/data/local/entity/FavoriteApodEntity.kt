package com.tech.maxclub.nasaapod.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tech.maxclub.nasaapod.domain.model.MediaType
import java.io.Serializable
import java.util.*

@Entity(tableName = "favorite_apod_table")
data class FavoriteApodEntity(
    @ColumnInfo(name = "date")
    @PrimaryKey val date: Date,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "media_type")
    val mediaType: MediaType,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "hdurl")
    val hdUrl: String? = null,

    @ColumnInfo(name = "copyright")
    val copyright: String? = null,

    @ColumnInfo(name = "is_new")
    val isNew: Boolean = true,

    @ColumnInfo(name = "position")
    val position: Int = -1,
) : Serializable