package com.tech.maxclub.nasaapod.presentation.home_pager.apod_pager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tech.maxclub.nasaapod.domain.model.ApodDate
import com.tech.maxclub.nasaapod.domain.usecase.GetServiceDateUseCase
import com.tech.maxclub.nasaapod.domain.util.InvalidServiceDateException
import com.tech.maxclub.nasaapod.domain.util.ServiceDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getServiceDate: GetServiceDateUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState(listOf(ApodDate.Today())))
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<HomeUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    var position: Int = 0
        private set

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnPositionChanged -> {
                position = event.position
            }
            is HomeEvent.OnItemLoaded -> {
                _uiState.value = HomeUiState(getUpdatedApodDates(event.apodDate))
            }
            is HomeEvent.OnStateReset -> {
                _uiState.value = HomeUiState(
                    listOf(ApodDate.Today(getServiceDate(ServiceDate.Today)))
                )
            }
            is HomeEvent.OnRandomDateClick -> {
                _uiState.value = HomeUiState(listOf(ApodDate.Random()))
            }
            is HomeEvent.OnDatePickerClick -> {
                viewModelScope.launch {
                    _uiEvent.emit(
                        HomeUiEvent.OnShowDatePicker(
                            startDate = getServiceDate(ServiceDate.Start),
                            endDate = getServiceDate(ServiceDate.Today),
                            isValid = getServiceDate::contains,
                        )
                    )
                }
            }
            is HomeEvent.OnDateSelected -> {
                _uiState.value = HomeUiState(listOf(ApodDate.From(event.date)))
            }
        }
    }

    private fun getUpdatedApodDates(updatedApodDate: ApodDate.From): List<ApodDate> =
        _uiState.value.apodDates
            .map { apodDate ->
                if (apodDate.id == updatedApodDate.id) updatedApodDate else apodDate
            }
            .toMutableList()
            .apply {
                indexOf(updatedApodDate).let { position ->
                    val date = updatedApodDate.date
                    if (position == size - 1) {
                        try {
                            add(ApodDate.From(getServiceDate(ServiceDate.NextTo(date))))
                        } catch (e: InvalidServiceDateException) {
                            e.printStackTrace()
                        }
                    }
                    if (position == 0) {
                        try {
                            add(
                                position,
                                ApodDate.From(getServiceDate(ServiceDate.PreviousTo(date)))
                            )
                        } catch (e: InvalidServiceDateException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
}