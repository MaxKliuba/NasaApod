package com.tech.maxclub.nasaapod.domain.util

import com.tech.maxclub.nasaapod.data.local.entity.FavoriteApodEntity
import com.tech.maxclub.nasaapod.data.remote.dto.ApodDto
import com.tech.maxclub.nasaapod.domain.model.Apod
import com.tech.maxclub.nasaapod.domain.model.FavoriteApod
import com.tech.maxclub.nasaapod.domain.model.ImageInfo
import com.tech.maxclub.nasaapod.domain.model.MediaType
import java.util.*

/*
 * ApodDto
 */
fun ApodDto.toApod(
    date: Date = this.date,
    title: String = this.title,
    explanation: String = this.explanation,
    mediaType: MediaType = this.mediaType,
    url: String = this.url,
    hdUrl: String? = this.hdUrl,
    copyright: String? = this.copyright,
    isFavorite: Boolean = false,
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

/*
 * Apod
 */
fun Apod.toFavoriteApod(
    date: Date = this.date,
    title: String = this.title,
    mediaType: MediaType = this.mediaType,
    url: String = this.url,
    hdUrl: String? = this.hdUrl,
    copyright: String? = this.copyright,
    isNew: Boolean = true,
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

/*
 * FavoriteApodEntity
 */
fun FavoriteApodEntity.toFavoriteApod(
    date: Date = this.date,
    title: String = this.title,
    mediaType: MediaType = this.mediaType,
    url: String = this.url,
    hdUrl: String? = this.hdUrl,
    copyright: String? = this.copyright,
    isNew: Boolean = this.isNew,
    position: Int = this.position,
): FavoriteApod =
    FavoriteApod(
        date = date,
        title = title,
        mediaType = mediaType,
        url = url,
        hdUrl = hdUrl,
        copyright = copyright,
        isNew = isNew,
        position = position,
    )

/*
 * FavoriteApod
 */
fun FavoriteApod.toFavoriteApodEntity(
    date: Date = this.date,
    title: String = this.title,
    mediaType: MediaType = this.mediaType,
    url: String = this.url,
    hdUrl: String? = this.hdUrl,
    copyright: String? = this.copyright,
    isNew: Boolean = this.isNew,
    position: Int = this.position,
): FavoriteApodEntity =
    FavoriteApodEntity(
        date = date,
        title = title,
        mediaType = mediaType,
        url = url,
        hdUrl = hdUrl,
        copyright = copyright,
        isNew = isNew,
        position = position,
    )

fun FavoriteApod.toApod(
    date: Date = this.date,
    title: String = this.title,
    explanation: String = "",
    mediaType: MediaType = this.mediaType,
    url: String = this.url,
    hdUrl: String? = this.hdUrl,
    copyright: String? = this.copyright,
    isFavorite: Boolean = true,
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