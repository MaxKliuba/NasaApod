package com.android.maxclub.nasaapod.presentation.favorites_pager.favorite_apod_pager

import com.android.maxclub.nasaapod.data.FavoriteApod

sealed class FavoritesUiEvent {
    data class OnItemDeleted(val favoriteApod: FavoriteApod) : FavoritesUiEvent()
    data class OnItemRestored(val favoriteApod: FavoriteApod, val position: Int) :
        FavoritesUiEvent()
}
