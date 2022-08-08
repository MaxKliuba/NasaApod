package com.android.maxclub.nasaapod.presentation.favorites_list

import com.android.maxclub.nasaapod.domain.model.FavoriteApod

sealed class FavoriteListEvent {
    data class OnItemClick(val favoriteApod: FavoriteApod) : FavoriteListEvent()
    data class OnItemDelete(val favoriteApod: FavoriteApod) : FavoriteListEvent()
    data class OnItemRestore(val favoriteApod: FavoriteApod) : FavoriteListEvent()
    data class OnLocalUpdate(val favoriteApods: List<FavoriteApod>) : FavoriteListEvent()
    data class OnUpdate(val favoriteApods: List<FavoriteApod>) : FavoriteListEvent()
}