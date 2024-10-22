package com.alltrails.restaurantdiscovery.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.alltrails.restaurantdiscovery.data.Restaurant
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.alltrails.restaurantdiscovery.R as R

@Composable
fun RestaurantScreen(
    modifier: Modifier = Modifier,
    viewState: RestaurantViewState,
    selectedMarker: Bitmap? = null,
    unselectedMarker: Bitmap? = null,
    onAction: (RestaurantsAction) -> Unit,
) {
    Column(
        modifier = modifier.background(color = colorResource(R.color.background_light))
    ) {
        when (viewState) {
            is RestaurantViewState.Content -> {
                RestaurantsContent(
                    selectedMarker = selectedMarker,
                    unselectedMarker = unselectedMarker,
                    location = viewState.location,
                    restaurants = viewState.restaurants,
                    selectedIndex = viewState.selectedIndex,
                    isListShowing = viewState.isListShowing,
                    onAction = onAction,
                )
            }
            is RestaurantViewState.Error -> {
                ErrorContent(onAction = onAction)
            }
            is RestaurantViewState.Loading -> {
                LoadingContent()
            }

            RestaurantViewState.NoPermissions -> {
                PermissionDialog()
                ErrorContent(onAction = onAction)
            }
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun PermissionDialog() {
    var isOpen by remember { mutableStateOf(true) }
    if (isOpen) {
        AlertDialog(
            onDismissRequest = { isOpen = false },
            title = {
                Text(text = stringResource(R.string.permissions_title))
            },
            text = {
                Text(text = stringResource(R.string.permissions_text))
            },
            confirmButton = {
                TextButton(onClick = {isOpen = false}) {
                    Text(text = stringResource(R.string.dismiss))
                }
            }
        )
    }
}

@Composable
private fun ErrorContent(
    modifier: Modifier = Modifier,
    onAction: (RestaurantsAction) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.error_title))
        Button(
            onClick = { onAction(RestaurantsAction.RetryButtonClicked)}
        ) {
            Text(text = stringResource(R.string.error_button_text))
        }
    }
}

@Composable
private fun RestaurantsContent(
    modifier: Modifier = Modifier,
    selectedMarker: Bitmap? = null,
    unselectedMarker: Bitmap? = null,
    location: LatLng,
    isListShowing: Boolean,
    restaurants: List<Restaurant>,
    selectedIndex: Int? = null,
    onAction: (RestaurantsAction) -> Unit,
) {
    RestaurantsSearchBar(
        onAction = onAction,
    )
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (isListShowing) {
            ListContent(
                restaurants = restaurants,
                onAction = onAction
            )
        } else {
            MapContent(
                location = location,
                restaurants = restaurants,
                selectedMarker = selectedMarker,
                unselectedMarker = unselectedMarker,
                selectedIndex = selectedIndex,
                onAction = onAction
            )
        }
        MapButton(
            isListShowing = isListShowing,
            onAction = onAction
        )
    }

}

@Composable
private fun MapButton(
    modifier: Modifier = Modifier,
    isListShowing: Boolean,
    onAction: (RestaurantsAction) -> Unit,
){
    val (icon, text) = if(isListShowing) {
        R.drawable.map to R.string.map_button
    } else {
        R.drawable.list to R.string.list_button
    }
    Button(
        modifier = modifier.padding(16.dp),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = colorResource(R.color.background_button)
        ),
        onClick = { onAction(RestaurantsAction.MapButtonClicked) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = colorResource(R.color.white)
            )
            Text(
                text = stringResource(text),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.white)
            )
        }
    }
}

@Composable
private fun ListContent(
    modifier: Modifier = Modifier,
    restaurants: List<Restaurant>,
    onAction: (RestaurantsAction) -> Unit
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(restaurants) { restaurant ->
            RestaurantCard(
                name = restaurant.name,
                rating = restaurant.rating,
                reviews = restaurant.reviews,
                supportingText = restaurant.supportingText,
                url = restaurant.image,
                id = restaurant.id,
                isFavorite = restaurant.isFavorite,
                onAction = onAction,
            )
        }
    }
}

@Composable
private fun MapContent(
    modifier: Modifier = Modifier,
    location: LatLng,
    restaurants: List<Restaurant>,
    selectedMarker: Bitmap?,
    unselectedMarker: Bitmap?,
    selectedIndex: Int?,
    onAction: (RestaurantsAction) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Map(
            location = location,
            restaurants = restaurants,
            selectedMarker = selectedMarker,
            unselectedMarker = unselectedMarker,
            selectedIndex = selectedIndex,
            onAction = onAction
        )

        if (selectedIndex != null) {
            val selectedRestaurant = restaurants[selectedIndex]

            RestaurantCard(
                modifier = Modifier.offset(y = (-96).dp),
                name = selectedRestaurant.name,
                rating = selectedRestaurant.rating,
                reviews = selectedRestaurant.reviews,
                supportingText = selectedRestaurant.supportingText,
                url = selectedRestaurant.image,
                id = selectedRestaurant.id,
                isFavorite = selectedRestaurant.isFavorite,
                onAction = onAction,
            )
        }
    }
}

@Composable
private fun Map(
    modifier: Modifier = Modifier,
    location: LatLng,
    restaurants: List<Restaurant>,
    selectedMarker: Bitmap?,
    unselectedMarker: Bitmap?,
    selectedIndex: Int?,
    onAction: (RestaurantsAction) -> Unit
) {

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 15f)
    }
    val mapUiSettings by remember { mutableStateOf(MapUiSettings()) }
    val mapProperties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }
    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = mapUiSettings,
        properties = mapProperties
    ) {
        restaurants.mapIndexed { index, restaurant ->
            RestaurantMarker(
                selectedMarker = selectedMarker,
                unselectedMarker = unselectedMarker,
                id = restaurant.id,
                location = restaurant.location,
                isSelected = selectedIndex == index,
                onAction = onAction,
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
private fun RestaurantMarker(
    selectedMarker: Bitmap? = null,
    unselectedMarker: Bitmap? = null,
    id: String,
    isSelected: Boolean,
    location: LatLng,
    onAction: (RestaurantsAction) -> Unit,
) {
    val markerBitmap = if(isSelected) {
        selectedMarker
    } else {
        unselectedMarker
    }
    Marker(
        state = MarkerState(position = location),
        icon = markerBitmap?.let { BitmapDescriptorFactory.fromBitmap(markerBitmap) },
        onClick = {
            onAction(RestaurantsAction.SelectMarker(id = id))
            false
        },
    )
}

@Composable
private fun RestaurantsSearchBar(
    modifier: Modifier = Modifier,
    onAction: (RestaurantsAction) -> Unit,
) {
    Column(
        modifier = modifier
            .background(color = colorResource(R.color.white))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.logo_lockup),
            contentDescription = stringResource(R.string.all_trails_lunch)
        )
        RestaurantsSearch(
            onAction = onAction,
        )
    }
}

@Composable
private fun RestaurantsSearch(
    modifier: Modifier = Modifier,
    onAction: (RestaurantsAction) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var text by remember { mutableStateOf(TextFieldValue("")) }

    TextField(
        value = text,
        onValueChange = { text = it },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50),
        placeholder = { Text(text = stringResource(R.string.search) ) },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.search),
                contentDescription = stringResource(R.string.search_icon)
            )
        },
        trailingIcon = {
            if (text.text.isNotEmpty()) {
                IconButton(onClick = {
                    text = TextFieldValue("")
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.clear_text)
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onAction(RestaurantsAction.SearchClicked(query = text.text))
                keyboardController?.hide()
            }
        ),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = colorResource(R.color.background_light),
            unfocusedContainerColor = colorResource(R.color.background_light),
        ),
    )
}

@Composable
private fun RestaurantCard(
    modifier: Modifier = Modifier,
    name: String,
    url: String? = null,
    rating: String,
    reviews: String,
    supportingText: String? = null,
    isFavorite: Boolean = false,
    id: String,
    onAction: (RestaurantsAction) -> Unit,
) {
    val bookmark = if(isFavorite) R.drawable.bookmark_saved else R.drawable.bookmark_resting

    Card(
        modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation( defaultElevation = 12.dp),
        colors = CardDefaults.cardColors().copy(containerColor = colorResource(R.color.white))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            Arrangement
                .spacedBy(space = 8.dp,),
        ) {
            AsyncImage(
                modifier = Modifier.size(80.dp),
                model = url,
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
            Column(modifier = Modifier.fillMaxWidth()){
                Headline(
                    name = name,
                    id =  id,
                    isFavorite = isFavorite,
                    bookmark = bookmark,
                    onAction = onAction,
                )
                RatingsAndReviews(rating, reviews)
                if (supportingText != null) {
                    Text(
                        text = supportingText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = colorResource(R.color.text_light)
                    )
                }
            }
        }
    }
}

@Composable
private fun Headline(
    name: String,
    onAction: (RestaurantsAction) -> Unit,
    id: String,
    isFavorite: Boolean,
    bookmark: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            maxLines = 1,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.text_dark),
        )
        Image(
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    onAction(
                        RestaurantsAction.BookmarkClicked(
                            id = id,
                            isAdd = !isFavorite,
                        )
                    )
                },
            painter = painterResource(bookmark),
            contentDescription = stringResource(bookmark)
        )
    }
}

@Composable
private fun RatingsAndReviews(rating: String, reviews: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(
            modifier = Modifier.size(16.dp),
            painter = painterResource(R.drawable.star),
            contentDescription = stringResource(R.string.star)

        )
        Text(
            text = rating,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = colorResource(R.color.text_dark)
        )
        Text(
            text = stringResource(R.string.dot_spacer),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = reviews,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = colorResource(R.color.text_light)
        )
    }
}
