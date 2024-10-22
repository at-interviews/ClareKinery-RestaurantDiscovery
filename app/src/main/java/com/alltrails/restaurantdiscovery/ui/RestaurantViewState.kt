package com.alltrails.restaurantdiscovery.ui

import com.alltrails.restaurantdiscovery.data.Restaurant
import com.google.android.gms.maps.model.LatLng

sealed class RestaurantViewState {

    data object Loading: RestaurantViewState()

    data object Error: RestaurantViewState()

    data object NoPermissions: RestaurantViewState()

    data class Content(
        val isListShowing: Boolean = false,
        val location: LatLng,
        val selectedIndex: Int? = null,
        val restaurants: List<Restaurant>
    ): RestaurantViewState()
}