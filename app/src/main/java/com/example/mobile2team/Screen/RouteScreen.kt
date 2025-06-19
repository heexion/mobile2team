package com.example.mobile2team.Screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Looper
import android.widget.Toast
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
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
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

@SuppressLint("MissingPermission")
fun startLocationUpdates(context: Context, onLocationUpdate: (LatLng) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val locationRequest = LocationRequest.create().apply {
        interval = 3000
        fastestInterval = 1000
        priority = Priority.PRIORITY_HIGH_ACCURACY
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let {
                onLocationUpdate(LatLng(it.latitude, it.longitude))
            }
        }
    }

    if (
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
    ) return

    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
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
    val cameraPositionState = rememberCameraPositionState()

    // 권한 허용 시 위치 추적 시작
    LaunchedEffect(permissionsState.allPermissionsGranted) {
        if (permissionsState.allPermissionsGranted) {
            locationSource.activate { /* no-op */ }
            startLocationUpdates(context) { location ->
                startLatLng = location
            }
        } else {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    // 위치 받아오면 카메라 이동
    LaunchedEffect(startLatLng) {
        startLatLng?.let { start ->
            val centerLat = (start.latitude + destinationLat) / 2
            val centerLng = (start.longitude + destinationLng) / 2
            val center = LatLng(centerLat, centerLng)
            val update = CameraUpdate.toCameraPosition(CameraPosition(center, 10.0))
            cameraPositionState.move(update)
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
                    val start = startLatLng
                    if (start != null && start.latitude.isFinite() && start.longitude.isFinite()) {
                        val nmapUri = Uri.parse(
                            "nmap://route/public" +
                                    "?slat=${start.latitude}" +
                                    "&slng=${start.longitude}" +
                                    "&sname=현재위치" +
                                    "&dlat=$destinationLat" +
                                    "&dlng=$destinationLng" +
                                    "&dname=${Uri.encode(destinationName)}" +
                                    "&appname=com.example.mobile2team"
                        )

                        val intent = Intent(Intent.ACTION_VIEW, nmapUri)


                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val fallbackUri = Uri.parse("https://map.naver.com/v5/directions")
                            Toast.makeText(
                                context,
                                "네이버 지도 앱이 없거나 실행에 실패했습니다. 웹으로 이동합니다.",
                                Toast.LENGTH_LONG
                            ).show()
                            context.startActivity(Intent(Intent.ACTION_VIEW, fallbackUri))
                        }
                    } else {
                        Toast.makeText(context, "현재 위치를 불러오는 중입니다.", Toast.LENGTH_SHORT).show()
                    }
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
