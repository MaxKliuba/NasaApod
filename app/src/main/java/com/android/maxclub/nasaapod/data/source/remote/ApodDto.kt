package com.android.maxclub.nasaapod.data.source.remote

import com.android.maxclub.nasaapod.data.util.MediaType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ApodDto(
    @Serializable(with = DateSerializer::class)
    @SerialName("date") val date: Date,

    @SerialName("title") val title: String,

    @SerialName("explanation") val explanation: String,

    @Serializable(with = MediaTypeSerializer::class)
    @SerialName("media_type") val mediaType: MediaType,

    @SerialName("url") val url: String,

    @SerialName("hdurl") val hdUrl: String? = null,

    @SerialName("copyright") val copyright: String? = null,
)