package com.tech.maxclub.nasaapod.presentation.favorites_list

import com.tech.maxclub.nasaapod.domain.model.FavoriteApod

sealed class FavoriteListUiState(val favoriteApods: List<FavoriteApod>) {
    class Loading(favoriteApods: List<FavoriteApod>) : FavoriteListUiState(favoriteApods)
    class Success(favoriteApods: List<FavoriteApod>) : FavoriteListUiState(favoriteApods)
}