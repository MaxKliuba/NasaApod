package com.android.maxclub.nasaapod.uistates

import com.android.maxclub.nasaapod.data.FavoriteApod

sealed class FavoritesViewPagerUiState {
    object Initializing : FavoritesViewPagerUiState()
    class DataChanged(val data: List<FavoriteApod>) : FavoritesViewPagerUiState()
}
