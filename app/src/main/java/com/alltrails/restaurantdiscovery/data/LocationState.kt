package com.alltrails.restaurantdiscovery.data

import com.google.android.gms.maps.model.LatLng

sealed class LocationState {
    data object NoPermission: LocationState()
    data object LocationLoading: LocationState()
    data object Error: LocationState()
    data class LocationAvailable(val location: LatLng): LocationState()
}