package com.example.currancyconvertor.models

import com.example.currancyconvertor.models.Currencies
import com.squareup.moshi.Json


data class CurrencyData(
    @Json(name = "Date") val date: String,
    @Json(name = "PreviousDate") val previousDate: String,
    @Json(name = "PreviousURL") val previousURL: String,
    @Json(name = "Timestamp") val timestamp: String,
    @Json(name = "Valute") val valute: Map<String, Currencies>
)
