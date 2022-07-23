package com.android.maxclub.nasaapod.data.source.local

import androidx.room.TypeConverter
import com.android.maxclub.nasaapod.data.MediaType
import java.util.*

class Converters {
    @TypeConverter
    fun fromMediaType(mediaType: MediaType?): Int? = mediaType?.ordinal

    @TypeConverter
    fun toMediaType(mediaTypeOrdinal: Int?): MediaType? =
        mediaTypeOrdinal?.let { MediaType.values()[it] }

    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? = millisSinceEpoch?.let { Date(it) }
}