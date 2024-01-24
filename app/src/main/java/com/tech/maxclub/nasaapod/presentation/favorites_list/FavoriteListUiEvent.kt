package com.tech.maxclub.nasaapod.presentation.favorites_list

import com.tech.maxclub.nasaapod.domain.model.FavoriteApod

sealed class FavoriteListUiEvent {
    data class OnItemClicked(val favoriteApod: FavoriteApod) : FavoriteListUiEvent()
    data class OnItemDeleted(val favoriteApod: FavoriteApod) : FavoriteListUiEvent()
}