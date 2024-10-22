package com.alltrails.restaurantdiscovery.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationResponse(
    @Json(name = "lat") val lat: Double,
    @Json(name = "lng") val lng: Double,
)