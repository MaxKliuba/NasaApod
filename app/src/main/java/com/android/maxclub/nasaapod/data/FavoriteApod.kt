package com.android.maxclub.nasaapod.data

import java.util.*

data class FavoriteApod(
    val url: String? = null,
    val title: String? = null,
    val date: Date,
    val copyright: String? = null,
    val position: Int = -1,
)
