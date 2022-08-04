package com.android.maxclub.nasaapod.data.util

import com.android.maxclub.nasaapod.data.Apod
import com.android.maxclub.nasaapod.data.FavoriteApod
import java.util.*

fun Apod.toFavoriteApod(
    date: Date = this.date,
    title: String = this.title,
    mediaType: MediaType = this.mediaType,
    url: String = this.url,
    hdUrl: String? = this.hdUrl,
    copyright: String? = this.copyright,
    isNew: Boolean = true
): FavoriteApod =
    FavoriteApod(
        date = date,
        title = title,
        mediaType = mediaType,
        url = url,
        hdUrl = hdUrl,
        copyright = copyright,
        isNew = isNew,
    )

fun FavoriteApod.toApod(
    date: Date = this.date,
    title: String = this.title,
    explanation: String = "",
    mediaType: MediaType = this.mediaType,
    url: String = this.url,
    hdUrl: String? = this.hdUrl,
    copyright: String? = this.copyright,
    isFavorite: Boolean = true
): Apod =
    Apod(
        date = date,
        title = title,
        explanation = explanation,
        mediaType = mediaType,
        url = url,
        hdUrl = hdUrl,
        copyright = copyright,
        isFavorite = isFavorite,
    )

fun Apod.toImageInfo(
    date: Date = this.date,
    title: String = this.title,
    url: String = this.url,
    hdUrl: String? = this.hdUrl,
    copyright: String? = this.copyright,
): ImageInfo =
    ImageInfo(
        date = date,
        title = title,
        url = url,
        hdUrl = hdUrl,
        copyright = copyright,
    )

fun FavoriteApod.toImageInfo(
    date: Date = this.date,
    title: String = this.title,
    url: String = this.url,
    hdUrl: String? = this.hdUrl,
    copyright: String? = this.copyright,
): ImageInfo =
    ImageInfo(
        date = date,
        title = title,
        url = url,
        hdUrl = hdUrl,
        copyright = copyright,
    )