package com.tech.maxclub.nasaapod.domain.util

import java.util.*

sealed class ServiceDate {
    object Start : ServiceDate()
    object Today : ServiceDate()
    data class NextTo(val date: Date) : ServiceDate()
    data class PreviousTo(val date: Date) : ServiceDate()
}