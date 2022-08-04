package com.android.maxclub.nasaapod.presentation.favorites_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.nasaapod.data.repository.FavoriteApodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class FavoriteListViewModel @Inject constructor(
    private val favoriteApodRepository: FavoriteApodRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<FavoriteListUiState>(
        FavoriteListUiState.Loading(emptyList())
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<FavoriteListUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var fetchJob: Job? = null
    private var updateJob: Job? = null

    init {
        fetchFavoriteApods()
    }

    fun onEvent(event: FavoriteListEvent) {
        when (event) {
            is FavoriteListEvent.OnItemClick -> {
                viewModelScope.launch {
                    updateJob?.join()
                    _uiEvent.emit(FavoriteListUiEvent.OnItemClicked(event.favoriteApod))
                }
            }
            is FavoriteListEvent.OnItemDelete -> {
                viewModelScope.launch {
                    if (favoriteApodRepository.removeFavoriteApod(event.favoriteApod)) {
                        _uiEvent.emit(FavoriteListUiEvent.OnItemDeleted(event.favoriteApod))
                    }
                }
            }
            is FavoriteListEvent.OnItemRestore -> {
                CoroutineScope(Dispatchers.Main).launch {
                    favoriteApodRepository.addFavoriteApod(event.favoriteApod)
                }
            }
            is FavoriteListEvent.OnLocalUpdate -> {
                _uiState.value = FavoriteListUiState.Success(
                    event.favoriteApods.sortedByDescending { it.position }
                )
            }
            is FavoriteListEvent.OnUpdate -> {
                updateJob = viewModelScope.launch {
                    favoriteApodRepository.updateFavoriteApods(*event.favoriteApods.toTypedArray())
                }
            }
        }
    }

    private fun fetchFavoriteApods() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            favoriteApodRepository.getFavoriteApods()
                .onStart {
                    _uiState.value = FavoriteListUiState.Loading(_uiState.value.favoriteApods)
                }.catch {
                    _uiState.value = FavoriteListUiState.Success(_uiState.value.favoriteApods)
                }.collect { favoriteApods ->
                    _uiState.value = FavoriteListUiState.Success(
                        favoriteApods.sortedByDescending { it.position }
                    )
                }
        }
    }
}