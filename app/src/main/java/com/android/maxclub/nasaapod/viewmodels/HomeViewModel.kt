package com.android.maxclub.nasaapod.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.nasaapod.data.ApodDate
import com.android.maxclub.nasaapod.data.ApodDateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apodDateRepository: ApodDateRepository
) : ViewModel() {
    private val _apodDates = MutableStateFlow<List<ApodDate>>(listOf(ApodDate.Today()))
    val apodDates: StateFlow<List<ApodDate>> = _apodDates

    init {
        viewModelScope.launch {
            apodDateRepository.getApodDates()
                .collect { apodDates ->
                    _apodDates.value = apodDates
                }
        }
    }

    fun addNewDate(newDate: ApodDate) =
        apodDateRepository.addNewDate(newDate)

    fun replaceAllWithNewDate(newDate: ApodDate) =
        apodDateRepository.replaceAllWithNewDate(newDate)
}