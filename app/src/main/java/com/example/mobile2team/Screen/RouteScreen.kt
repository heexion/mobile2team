package com.example.mobile2team.Screen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.PathOverlay
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource
import com.naver.maps.map.compose.rememberMarkerState


private const val LOCATION_PERMISSION_REQUEST_CODE = 1000

fun getCurrentLocation(context: Context, onResult: (LatLng?) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    if (
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
    ) {
        onResult(null)
        return
    }

    fusedLocationClient.lastLocation
        .addOnSuccessListener {
            it?.let { location ->
                onResult(LatLng(location.latitude, location.longitude))
            } ?: onResult(null)
        }
        .addOnFailureListener {
            onResult(null)
        }
}

@OptIn(ExperimentalNaverMapApi::class, ExperimentalPermissionsApi::class)
@Composable
fun RouteScreen(
    destinationName: String,
    destinationLat: Double,
    destinationLng: Double,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var startLatLng by remember { mutableStateOf<LatLng?>(null) }
    val endLatLng = LatLng(destinationLat, destinationLng)

    val permissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val locationSource = rememberFusedLocationSource()


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(endLatLng, 12.0)
    }

    LaunchedEffect(permissionsState.allPermissionsGranted) {
        if (permissionsState.allPermissionsGranted) {
            locationSource.activate { /* no-op listener */ }
            getCurrentLocation(context) { location ->
                startLatLng = location
            }
        } else {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("길찾기", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (startLatLng == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            val startText = "내 위치 (${startLatLng!!.latitude}, ${startLatLng!!.longitude})"

            OutlinedTextField(
                value = startText,
                onValueChange = {},
                label = { Text("출발지") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = destinationName,
                onValueChange = {},
                label = { Text("도착지") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val uri = Uri.parse("https://map.naver.com/v5/directions/${startLatLng!!.latitude},${startLatLng!!.longitude}/${destinationLat},${destinationLng}")
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("길찾기 실행")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                NaverMap(
                    cameraPositionState = cameraPositionState,
                    locationSource = locationSource,
                    properties = MapProperties(locationTrackingMode = LocationTrackingMode.Follow),
                    uiSettings = MapUiSettings(isLocationButtonEnabled = true)
                ) {
                    PathOverlay(
                        coords = listOf(startLatLng!!, endLatLng),
                        color = Color.Blue,
                        width = 5.dp
                    )
                    Marker(
                        state = rememberMarkerState(position = startLatLng!!),
                        captionText = "출발지"
                    )
                    Marker(
                        state = rememberMarkerState(position = endLatLng),
                        captionText = "도착지"
                    )
                }
            }
        }
    }
}
