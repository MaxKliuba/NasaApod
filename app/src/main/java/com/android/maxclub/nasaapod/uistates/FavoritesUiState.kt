package com.android.maxclub.nasaapod.uistates

import com.android.maxclub.nasaapod.data.FavoriteApod

sealed class FavoritesUiState(val favoriteApods: List<FavoriteApod>) {
    object Initializing : FavoritesUiState(emptyList())
    class DataChanged(favoriteApods: List<FavoriteApod>) : FavoritesUiState(favoriteApods)
}
