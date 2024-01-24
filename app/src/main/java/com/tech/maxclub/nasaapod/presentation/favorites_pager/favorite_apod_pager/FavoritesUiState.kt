package com.tech.maxclub.nasaapod.presentation.favorites_pager.favorite_apod_pager

import com.tech.maxclub.nasaapod.domain.model.FavoriteApod

data class FavoritesUiState(
    val favoriteApods: List<FavoriteApod>,
)
