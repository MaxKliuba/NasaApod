package com.android.maxclub.nasaapod.data.source.remote

import com.android.maxclub.nasaapod.data.util.MediaType
import com.android.maxclub.nasaapod.util.DEFAULT_DATE_LOCALE
import com.android.maxclub.nasaapod.util.DEFAULT_DATE_PATTERN
import com.android.maxclub.nasaapod.util.formatDate
import com.android.maxclub.nasaapod.util.parseDate
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

class DateSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Date =
        parseDate(decoder.decodeString(), DEFAULT_DATE_PATTERN, DEFAULT_DATE_LOCALE)!!

    override fun serialize(encoder: Encoder, value: Date) =
        encoder.encodeString(formatDate(value, DEFAULT_DATE_PATTERN, DEFAULT_DATE_LOCALE))
}

class MediaTypeSerializer : KSerializer<MediaType> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("MediaType", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): MediaType =
        decoder.decodeString().let { value ->
            MediaType.values().firstOrNull { it.value == value } ?: MediaType.VIDEO
        }

    override fun serialize(encoder: Encoder, value: MediaType) =
        encoder.encodeString(value.value)
}