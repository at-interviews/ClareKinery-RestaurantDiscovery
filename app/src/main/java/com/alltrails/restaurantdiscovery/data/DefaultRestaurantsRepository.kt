package com.alltrails.restaurantdiscovery.data

import com.alltrails.restaurantdiscovery.api.GooglePlacesApiService
import com.alltrails.restaurantdiscovery.api.PlaceResponse
import com.google.android.gms.maps.model.LatLng
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

private const val RADIUS = 1000

class DefaultRestaurantsRepository @Inject constructor(
    private val googlePlacesService: GooglePlacesApiService,
): RestaurantsRepository {

    override suspend fun fetchNearbySearch(
        location: String,
        apiKey: String,
    ): List<Restaurant> {
        return googlePlacesService.getNearbyPlaces(
            location = location,
            radius = RADIUS,
            apiKey = apiKey
        ).results.map { place ->
            placeToRestaurant(place, apiKey)
        }
    }

    override suspend fun fetchTextSearch(
        query: String,
        location: String,
        apiKey: String
    ): List<Restaurant> {
        return googlePlacesService.getTextSearch(
            query = query,
            radius = RADIUS,
            location = location,
            apiKey = apiKey
        ).results.map { place ->
            placeToRestaurant(place, apiKey)
        }
    }

    private fun placeToRestaurant(place: PlaceResponse, apiKey: String): Restaurant {
        return Restaurant(
            id = place.placeId,
            name = place.name,
            rating = place.rating.toString(),
            reviews = formatReviews(place.userRatingsTotal),
            location = LatLng(place.geometry.location.lat, place.geometry.location.lng),
            image = getGooglePlaceImageUrl(
                photoReference = place.photos?.getOrNull(0)?.photoReference,
                apiKey = apiKey
            ),
            supportingText = place.address ?: place.vicinity
        )
    }

    private fun getGooglePlaceImageUrl(
        photoReference: String?,
        apiKey: String,
        maxWidth: Int = 400
    ): String? {
        return if (photoReference.isNullOrEmpty()) {
            null
        } else {
            "https://maps.googleapis.com/maps/api/place/photo" +
                    "?maxwidth=$maxWidth" +
                    "&photo_reference=$photoReference" +
                    "&key=$apiKey"
        }
    }

    private fun formatReviews(number: Int): String {
        return "(${NumberFormat.getNumberInstance(Locale.US).format(number)})"
    }
}