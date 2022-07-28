package com.android.maxclub.nasaapod.data

import java.io.Serializable
import java.util.*

data class ImageInfo(
    val date: Date,
    val title: String,
    val url: String,
    val copyright: String? = null
) : Serializable