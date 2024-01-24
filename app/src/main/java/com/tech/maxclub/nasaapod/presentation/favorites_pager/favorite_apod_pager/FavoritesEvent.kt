package com.tech.maxclub.nasaapod.presentation.favorites_pager.favorite_apod_pager

import com.tech.maxclub.nasaapod.domain.model.FavoriteApod

sealed class FavoritesEvent {
    data class OnPositionChanged(val position: Int) : FavoritesEvent()
    data class OnItemInserted(val favoriteApods: List<FavoriteApod>) : FavoritesEvent()
    data class OnItemDeleted(val favoriteApod: FavoriteApod) : FavoritesEvent()
    data class OnItemRestore(val favoriteApod: FavoriteApod) : FavoritesEvent()
}
