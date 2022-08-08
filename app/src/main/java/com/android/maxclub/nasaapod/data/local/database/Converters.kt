package com.android.maxclub.nasaapod.data.local.database

import androidx.room.TypeConverter
import com.android.maxclub.nasaapod.domain.model.MediaType
import com.android.maxclub.nasaapod.util.DEFAULT_DATE_LOCALE
import com.android.maxclub.nasaapod.util.DEFAULT_DATE_PATTERN
import com.android.maxclub.nasaapod.util.formatDate
import com.android.maxclub.nasaapod.util.parseDate
import java.util.*

class Converters {
    @TypeConverter
    fun fromMediaType(mediaType: MediaType?): String? = mediaType?.name

    @TypeConverter
    fun toMediaType(mediaTypeName: String?): MediaType? =
        mediaTypeName?.let { MediaType.valueOf(it) }

    @TypeConverter
    fun fromDate(date: Date?): String? =
        date?.let { formatDate(it, DEFAULT_DATE_PATTERN, DEFAULT_DATE_LOCALE) }

    @TypeConverter
    fun toDate(formattedDate: String?): Date? =
        formattedDate?.let { parseDate(it, DEFAULT_DATE_PATTERN, DEFAULT_DATE_LOCALE) }
}