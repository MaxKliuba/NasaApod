package com.tech.maxclub.nasaapod.domain.model

import java.io.Serializable
import java.util.*

data class FavoriteApod(
    val date: Date,
    val title: String,
    val mediaType: MediaType,
    val url: String,
    val hdUrl: String? = null,
    val copyright: String? = null,
    val isNew: Boolean = true,
    val position: Int = -1,
) : Serializable