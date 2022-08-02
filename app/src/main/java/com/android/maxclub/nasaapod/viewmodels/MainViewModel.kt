package com.android.maxclub.nasaapod.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.nasaapod.data.repository.FavoriteApodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val favoriteApodRepository: FavoriteApodRepository,
) : ViewModel() {
    private val _newFavoritesCount: MutableStateFlow<Int> = MutableStateFlow(0)
    val newFavoritesCount: StateFlow<Int> = _newFavoritesCount

    init {
        viewModelScope.launch {
            favoriteApodRepository.getFavoriteApods()
                .map { favorites ->
                    favorites.count { it.isNew }
                }.collect { count ->
                    _newFavoritesCount.value = count
                }
        }
    }
}