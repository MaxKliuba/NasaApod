package com.android.maxclub.nasaapod.presentation.home_pager.apod_pager

import com.google.android.material.datepicker.CalendarConstraints
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class ApodServiceDateValidator(val isValid: (Date) -> Boolean) : CalendarConstraints.DateValidator {
    override fun isValid(utcTimeMillis: Long): Boolean =
        isValid(Date(utcTimeMillis))
}