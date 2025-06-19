package com.example.mobile2team.Screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.example.mobile2team.Data.assets.toFacilityDetail
import com.example.mobile2team.Data.model.FacilityDetail
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberMarkerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MapScreen(
    searchQuery: String,
    selectedFacility: MutableState<FacilityDetail?>,
    onFacilitySelected: (FacilityDetail) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val currentLocation = remember { mutableStateOf<LatLng?>(null) }

    // 현재 위치 가져오기
    LaunchedEffect(Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentLocation.value = LatLng(it.latitude, it.longitude)
                }
            }
        }
    }

    val facilityDetails = remember {
        val jsonString = context.assets.open("facility.json").bufferedReader().use { it.readText() }
        val gson = Gson()
        val vworldResponse = gson.fromJson(jsonString, com.example.mobile2team.Data.assets.VWorldResponse::class.java)
        mutableStateOf(
            vworldResponse.response.result.featureCollection.features.map { it.toFacilityDetail() }
        )
    }

    val sortedFacilities = remember(currentLocation.value, searchQuery) {
        val base = currentLocation.value ?: LatLng(37.5408, 127.0793)
        facilityDetails.value
            .filter { it.name?.contains(searchQuery, ignoreCase = true) == true && it.latitude != null && it.longitude != null }
            .sortedBy { LatLng(it.latitude!!, it.longitude!!).distanceTo(base) }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(
            currentLocation.value ?: sortedFacilities.firstOrNull()?.let { LatLng(it.latitude ?: 0.0, it.longitude ?: 0.0) }
            ?: LatLng(37.5408, 127.0793),
            13.0
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        NaverMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(isScrollGesturesEnabled = true)
        ) {
            sortedFacilities.forEach { facility ->
                if (facility.latitude != null && facility.longitude != null) {
                    Marker(
                        state = rememberMarkerState(position = LatLng(facility.latitude!!, facility.longitude!!)),
                        captionText = facility.name,
                        onClick = {
                            selectedFacility.value = facility
                            onFacilitySelected(facility)
                            true
                        }
                    )
                }
            }
        }

        if (sortedFacilities.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sortedFacilities) { facility ->
                    Card(
                        modifier = Modifier
                            .width(280.dp)
                            .clickable {
                                selectedFacility.value = facility
                                onFacilitySelected(facility)
                                if (facility.latitude != null && facility.longitude != null) {
                                    coroutineScope.launch {
                                        cameraPositionState.move(
                                            CameraUpdate.toCameraPosition(
                                                CameraPosition(
                                                    LatLng(facility.latitude!!, facility.longitude!!),
                                                    15.0
                                                )
                                            )
                                        )
                                    }
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = facility.name ?: "",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = facility.address ?: "",
                                fontSize = 14.sp,
                                color = Color.DarkGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = facility.phoneNumber ?: "전화번호 정보 없음",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}