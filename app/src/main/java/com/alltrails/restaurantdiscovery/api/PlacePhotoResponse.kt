package com.alltrails.restaurantdiscovery.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlacePhotoResponse(
    @Json(name = "photo_reference") val photoReference: String? = null,
)