package com.tech.maxclub.nasaapod.domain.model

import java.io.Serializable
import java.util.*

data class Apod(
    val date: Date,
    val title: String,
    val explanation: String,
    val mediaType: MediaType,
    val url: String,
    val hdUrl: String? = null,
    val copyright: String? = null,
    val isFavorite: Boolean = false,
) : Serializable