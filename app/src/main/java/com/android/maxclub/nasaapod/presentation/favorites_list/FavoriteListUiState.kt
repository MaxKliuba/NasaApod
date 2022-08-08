package com.android.maxclub.nasaapod.presentation.favorites_list

import com.android.maxclub.nasaapod.domain.model.FavoriteApod

sealed class FavoriteListUiState(val favoriteApods: List<FavoriteApod>) {
    class Loading(favoriteApods: List<FavoriteApod>) : FavoriteListUiState(favoriteApods)
    class Success(favoriteApods: List<FavoriteApod>) : FavoriteListUiState(favoriteApods)
}