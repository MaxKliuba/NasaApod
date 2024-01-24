package com.tech.maxclub.nasaapod.data.remote.api

import com.tech.maxclub.nasaapod.domain.model.MediaType
import com.tech.maxclub.nasaapod.util.DEFAULT_DATE_LOCALE
import com.tech.maxclub.nasaapod.util.DEFAULT_DATE_PATTERN
import com.tech.maxclub.nasaapod.util.formatDate
import com.tech.maxclub.nasaapod.util.parseDate
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