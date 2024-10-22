package com.alltrails.restaurantdiscovery.data

interface RestaurantsRepository {

    suspend fun fetchNearbySearch(
        location: String,
        apiKey: String,
    ): List<Restaurant>

    suspend fun fetchTextSearch(
        query: String,
        location: String,
        apiKey: String,
    ): List<Restaurant>
}