package com.alltrails.restaurantdiscovery.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GooglePlacesResponse(
    @Json(name = "results") val results: List<PlaceResponse>,
    @Json(name = "status") val status: String
)