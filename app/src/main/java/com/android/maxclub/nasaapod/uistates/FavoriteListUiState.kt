package com.android.maxclub.nasaapod.uistates

import com.android.maxclub.nasaapod.data.FavoriteApod

sealed class FavoriteListUiState {
    object Initializing : FavoriteListUiState()
    object Loading : FavoriteListUiState()
    class Success(val data: List<FavoriteApod>) : FavoriteListUiState()
    class Error(val exception: Throwable) : FavoriteListUiState()
}
