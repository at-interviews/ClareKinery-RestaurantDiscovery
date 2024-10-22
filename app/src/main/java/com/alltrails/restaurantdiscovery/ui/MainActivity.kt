package com.alltrails.restaurantdiscovery.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import com.alltrails.restaurantdiscovery.BuildConfig
import com.alltrails.restaurantdiscovery.data.LocationState
import com.alltrails.restaurantdiscovery.ui.theme.RestaurantDiscoveryTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import com.alltrails.restaurantdiscovery.R as R

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: RestaurantViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        viewModel.getLocation.observe(this, Observer { getCurrentLocation() })

        setApiKey()
        requestLocationPermissions()
        enableEdgeToEdge()
        setContent {
            val viewState by viewModel.viewState.collectAsState()
            RestaurantDiscoveryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RestaurantScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewState = viewState,
                        selectedMarker = getBitmapFromDrawable(
                            drawableResId = R.drawable.pin_selected
                        ),
                        unselectedMarker = getBitmapFromDrawable(
                            drawableResId = R.drawable.pin_resting
                        ),
                        onAction = { action -> viewModel.processAction(action) }
                    )
                }
            }
        }
    }

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            viewModel.processLocationUpdate(LocationState.NoPermission)
        }
    }

    private fun requestLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }
            else -> {
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        viewModel.processLocationUpdate(
                            LocationState.LocationAvailable(
                                location = LatLng(location.latitude, location.longitude)
                            )
                        )
                    } else {
                        viewModel.processLocationUpdate(LocationState.Error)
                    }
                }
                .addOnFailureListener {
                    viewModel.processLocationUpdate(LocationState.Error)
                }
        } else {
            viewModel.processLocationUpdate(LocationState.NoPermission)
        }
    }

    private fun setApiKey() {
        val apiKey = BuildConfig.PLACES_API_KEY

        if (apiKey.isEmpty()) {
            Log.e("Places test", "No api key")
            finish()
        } else {
            viewModel.setApiKey(apiKey)
        }
    }

    private fun getBitmapFromDrawable(
        drawableResId: Int,
        width: Int = 100,
        height: Int = 100
    ): Bitmap? {
        return ContextCompat.getDrawable(this, drawableResId)?.toBitmap(width, height)
    }
}