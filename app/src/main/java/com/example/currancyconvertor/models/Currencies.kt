package com.example.currancyconvertor.models

import com.squareup.moshi.Json

class Currencies(
    @Json(name = "ID") val id: String,
    @Json(name = "NumCode") val numCode: String,
    @Json(name = "CharCode") val charCode: String,
    @Json(name = "Nominal") val nominal: Int,
    @Json(name = "Name") val name: String,
    @Json(name = "Value") val value: Float,
    @Json(name = "Previous") val previous: Float,
)
