package com.alltrails.restaurantdiscovery.api

import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesApiService {
    @GET("maps/api/place/nearbysearch/json")
    suspend fun getNearbyPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String = "restaurant",
        @Query("key") apiKey: String
    ): GooglePlacesResponse

    @GET("maps/api/place/textsearch/json")
    suspend fun getTextSearch(
        @Query("query") query: String,
        @Query("location") location: String?,
        @Query("radius") radius: Int,
        @Query("key") apiKey: String
    ): GooglePlacesResponse
}
