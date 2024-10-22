package com.alltrails.restaurantdiscovery.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceResponse(
    @Json(name = "name") val name: String,
    @Json(name = "place_id") val placeId: String,
    @Json(name = "geometry") val geometry: GeometryResponse,
    @Json(name = "vicinity") val vicinity: String? = null,
    @Json(name = "formatted_address") val address: String? = null,
    @Json(name = "rating") val rating: Float,
    @Json(name = "user_ratings_total") val userRatingsTotal: Int,
    @Json(name = "photos") val photos: List<PlacePhotoResponse>? = emptyList(),
)