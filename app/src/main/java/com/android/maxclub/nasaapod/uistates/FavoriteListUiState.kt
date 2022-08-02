package com.android.maxclub.nasaapod.uistates

import com.android.maxclub.nasaapod.data.FavoriteApod

sealed class FavoriteListUiState(val favoriteApods: List<FavoriteApod>) {
    object Initializing : FavoriteListUiState(emptyList())
    class Loading(favoriteApods: List<FavoriteApod>) : FavoriteListUiState(favoriteApods)
    class DataChanged(favoriteApods: List<FavoriteApod>) : FavoriteListUiState(favoriteApods)
}
