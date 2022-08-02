package com.android.maxclub.nasaapod.viewmodels

import androidx.lifecycle.ViewModel
import com.android.maxclub.nasaapod.data.ApodDate
import com.android.maxclub.nasaapod.utils.getNextDate
import com.android.maxclub.nasaapod.utils.getPrevDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _apodDates = MutableStateFlow<List<ApodDate>>(listOf(ApodDate.Today()))
    val apodDates: StateFlow<List<ApodDate>> = _apodDates
    var currentPosition: Int = 0

    fun resetApodDates() {
        _apodDates.value = listOf(ApodDate.Today())
    }

    fun replaceAllWithNewApodDate(vararg newApodDates: ApodDate) {
        _apodDates.value = newApodDates.toList()
    }

    fun updateApodDate(updatedApodDate: ApodDate.From) {
        _apodDates.value = _apodDates.value
            .map { apodDate ->
                if (apodDate.id == updatedApodDate.id) updatedApodDate else apodDate
            }
            .toMutableList()
            .apply {
                indexOf(updatedApodDate).let { position ->
                    val date = updatedApodDate.date
                    if (position == size - 1) add(ApodDate.From(getNextDate(date)))
                    if (position == 0) add(position, ApodDate.From(getPrevDate(date)))
                }
            }
    }
}