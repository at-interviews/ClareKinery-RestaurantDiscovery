package com.alltrails.restaurantdiscovery.data

import com.google.android.gms.maps.model.LatLng

data class Restaurant(
    val id: String,
    val name: String,
    val image: String? = null,
    val rating: String,
    val reviews: String,
    val supportingText: String? = null,
    val location: LatLng,
    val isFavorite: Boolean = false,
)