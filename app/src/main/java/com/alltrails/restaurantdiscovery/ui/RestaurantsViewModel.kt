package com.alltrails.restaurantdiscovery.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.alltrails.restaurantdiscovery.data.LocationState
import com.alltrails.restaurantdiscovery.data.Restaurant
import com.alltrails.restaurantdiscovery.data.RestaurantsRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RestaurantViewModel @Inject constructor(
    private val repository: RestaurantsRepository,
) : ViewModel() {
    private lateinit var API_KEY: String

    private val _getLocation = MutableLiveData<Unit>()
    val getLocation: LiveData<Unit> get() = _getLocation

    private val _viewStateFlow = MutableStateFlow<RestaurantViewState>(RestaurantViewState.Loading)
    val viewState = _viewStateFlow.asStateFlow()

    private val _favorites = MutableStateFlow<List<String>>(emptyList())
    private val favorites: MutableStateFlow<List<String>> = _favorites

    internal fun setApiKey(apiKey: String) {
        API_KEY = apiKey
    }

    internal fun processLocationUpdate(locationState: LocationState) {
        viewModelScope.launch {
            when (locationState) {
                is LocationState.NoPermission -> {
                    _viewStateFlow.update { RestaurantViewState.NoPermissions }
                }
                LocationState.Error -> {
                    _viewStateFlow.update { RestaurantViewState.Error }
                }
                is LocationState.LocationAvailable -> {
                    fetchPlaces(location = locationState.location)
                }
                is LocationState.LocationLoading -> {
                    _viewStateFlow.update { RestaurantViewState.Loading }
                }
            }
        }
    }

    internal fun processAction(action: RestaurantsAction) {
        viewModelScope.launch {
            when(action) {
                is RestaurantsAction.SearchClicked -> {
                    if (viewState.value is RestaurantViewState.Content) {
                        fetchPlaces(
                            location = (viewState.value as RestaurantViewState.Content).location,
                            query = action.query,
                            isListShowing = (viewState.value as RestaurantViewState.Content).isListShowing
                        )
                    }
                }

                is RestaurantsAction.SelectMarker -> {
                    _viewStateFlow.updateContentState { viewState ->
                        val index = viewState.restaurants.indexOfFirst { restaurant ->
                            restaurant.id == action.id
                        }
                        viewState.copy(selectedIndex = index)
                    }
                }

                is RestaurantsAction.BookmarkClicked -> {
                    if(action.isAdd) {
                        _favorites.value += action.id
                    } else {
                        _favorites.value = _favorites.value.filterNot { it == action.id }
                    }

                    _viewStateFlow.updateContentState { viewState ->
                        val restaurants = viewState.restaurants.map { restaurant ->
                            if(restaurant.id == action.id) {
                                restaurant.copy(isFavorite = !restaurant.isFavorite)
                            } else {
                                restaurant
                            }
                        }
                        viewState.copy(restaurants = restaurants)
                    }
                }

                RestaurantsAction.RetryButtonClicked -> {
                    _viewStateFlow.update { RestaurantViewState.Loading }
                    delay(2000)
                    _getLocation.value = Unit
                }

                RestaurantsAction.MapButtonClicked -> {
                    _viewStateFlow.updateContentState { viewState ->
                        viewState.copy(isListShowing = !viewState.isListShowing)
                    }
                }
            }
        }
    }

    private fun fetchPlaces(
        location: LatLng,
        query: String? = null,
        isListShowing: Boolean = false,
    ) {
        viewModelScope.launch {
            _viewStateFlow.update {
                try {
                    val response = if (query.isNullOrEmpty()) {
                        repository.fetchNearbySearch(
                            location = "${location.latitude},${location.longitude}",
                            apiKey = API_KEY
                        )
                    } else {
                        repository.fetchTextSearch(
                            query = query,
                            location = "${location.latitude},${location.longitude}",
                            apiKey = API_KEY
                        )
                    }

                    if (response.isNotEmpty()) {
                        RestaurantViewState.Content(
                            isListShowing = isListShowing,
                            location = location,
                            restaurants = setFavorites(response)
                        )
                    } else {
                        RestaurantViewState.Error
                    }
                } catch (e: Exception) {
                    RestaurantViewState.Error
                }
            }
        }
    }

    private fun setFavorites(restaurants: List<Restaurant>): List<Restaurant> {
        return restaurants.map { restaurant ->
            restaurant.copy(isFavorite = favorites.value.contains(restaurant.id))
        }
    }

    private fun MutableStateFlow<RestaurantViewState>.updateContentState(
        function: (RestaurantViewState.Content) -> RestaurantViewState,
    ) {
        this.update { viewState ->
            if (viewState is RestaurantViewState.Content) {
                function(viewState)
            } else {
                viewState
            }
        }
    }
}
