package com.alltrails.restaurantdiscovery.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeometryResponse(
    @Json(name = "location") val location: LocationResponse
)
