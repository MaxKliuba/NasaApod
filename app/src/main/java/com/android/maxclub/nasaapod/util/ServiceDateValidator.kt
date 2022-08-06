package com.android.maxclub.nasaapod.util

import com.google.android.material.datepicker.CalendarConstraints
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class ServiceDateValidator : CalendarConstraints.DateValidator {
    override fun isValid(utcTimeMillis: Long): Boolean =
        ServiceDateManager.isDateValid(Date(utcTimeMillis))
}