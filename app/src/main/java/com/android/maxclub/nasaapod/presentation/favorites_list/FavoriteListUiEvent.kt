package com.android.maxclub.nasaapod.presentation.favorites_list

import com.android.maxclub.nasaapod.data.FavoriteApod

sealed class FavoriteListUiEvent {
    data class OnItemClicked(val favoriteApod: FavoriteApod) : FavoriteListUiEvent()
    data class OnItemDeleted(val favoriteApod: FavoriteApod) : FavoriteListUiEvent()
}