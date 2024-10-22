package com.alltrails.restaurantdiscovery.ui

sealed class RestaurantsAction {

    data class SearchClicked(val query: String) : RestaurantsAction()

    data class SelectMarker(val id: String): RestaurantsAction()

    data object RetryButtonClicked : RestaurantsAction()

    data object MapButtonClicked: RestaurantsAction()

    data class BookmarkClicked(val id: String, val isAdd: Boolean): RestaurantsAction()

}