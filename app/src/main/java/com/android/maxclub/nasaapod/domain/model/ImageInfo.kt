package com.android.maxclub.nasaapod.domain.model

import java.io.Serializable
import java.util.*

data class ImageInfo(
    val date: Date,
    val title: String,
    val url: String,
    val hdUrl: String? = null,
    val copyright: String? = null
) : Serializable