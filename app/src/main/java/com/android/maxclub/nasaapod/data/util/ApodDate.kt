package com.android.maxclub.nasaapod.data.util

import java.io.Serializable
import java.util.*

sealed class ApodDate(val id: UUID) : Serializable {
    class Today(id: UUID = UUID.randomUUID()) : ApodDate(id)
    class From(val date: Date, id: UUID = UUID.randomUUID()) : ApodDate(id)
    class Random(id: UUID = UUID.randomUUID()) : ApodDate(id)
}