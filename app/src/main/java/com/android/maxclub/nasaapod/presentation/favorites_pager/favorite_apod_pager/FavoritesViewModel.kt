package com.android.maxclub.nasaapod.presentation.favorites_pager.favorite_apod_pager

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.nasaapod.domain.model.FavoriteApod
import com.android.maxclub.nasaapod.domain.usecase.ApodUseCases
import com.android.maxclub.nasaapod.presentation.favorites_pager.favorite_apod_pager.FavoritesViewPagerFragment.Companion.ARG_FAVORITE_APOD
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val apodUseCases: ApodUseCases,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        FavoritesUiState(
            savedStateHandle.get<FavoriteApod>(ARG_FAVORITE_APOD)?.let { favoriteApod ->
                listOf(favoriteApod)
            } ?: emptyList()
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<FavoritesUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    var position: Int = 0
        private set
    private var restoredItem: FavoriteApod? = null

    init {
        fetchFavoriteApods()
    }

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.OnItemInserted -> {
                restoredItem?.let { favoriteApod ->
                    event.favoriteApods.indexOf(favoriteApod)
                        .takeIf { position ->
                            position >= 0
                        }?.let { position ->
                            restoredItem = null
                            viewModelScope.launch {
                                delay(10)
                                _uiEvent.emit(
                                    FavoritesUiEvent.OnItemRestored(
                                        favoriteApod,
                                        position
                                    )
                                )
                            }
                        }
                }
            }
            is FavoritesEvent.OnPositionChanged -> {
                position = event.position
            }
            is FavoritesEvent.OnItemDeleted -> {
                viewModelScope.launch {
                    _uiEvent.emit(FavoritesUiEvent.OnItemDeleted(event.favoriteApod))
                }
            }
            is FavoritesEvent.OnItemRestore -> {
                CoroutineScope(Dispatchers.Main).launch {
                    apodUseCases.addFavoriteApod(event.favoriteApod)
                    restoredItem = event.favoriteApod
                }
            }
        }
    }

    private fun fetchFavoriteApods() {
        viewModelScope.launch {
            apodUseCases.getFavoriteApods()
                .collect { favoriteApods ->
                    _uiState.value = _uiState.value.copy(favoriteApods = favoriteApods)
                }
        }
    }
}