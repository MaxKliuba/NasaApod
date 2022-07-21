package com.android.maxclub.nasaapod.data.source.local

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? = millisSinceEpoch?.let { Date(it) }
}