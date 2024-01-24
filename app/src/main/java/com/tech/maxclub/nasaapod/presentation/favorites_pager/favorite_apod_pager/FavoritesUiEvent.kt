package com.tech.maxclub.nasaapod.presentation.favorites_pager.favorite_apod_pager

import com.tech.maxclub.nasaapod.domain.model.FavoriteApod

sealed class FavoritesUiEvent {
    data class OnItemDeleted(val favoriteApod: FavoriteApod) : FavoritesUiEvent()
    data class OnItemRestored(val favoriteApod: FavoriteApod, val position: Int) :
        FavoritesUiEvent()
}
