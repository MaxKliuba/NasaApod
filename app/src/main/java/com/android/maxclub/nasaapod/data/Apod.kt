package com.android.maxclub.nasaapod.data

import com.google.gson.annotations.SerializedName
import java.util.*

data class Apod(
    @SerializedName("url") val url: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("explanation") val explanation: String? = null,
    @SerializedName("date") val date: Date,
    @SerializedName("copyright") val copyright: String? = null,
    val isFavorite: Boolean = false,
    var isImageLoaded: Boolean = false,
)