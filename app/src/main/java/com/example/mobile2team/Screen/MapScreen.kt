package com.example.mobile2team.Screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.NaverMap
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberMarkerState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.mobile2team.Data.assets.FeatureCollection
import com.example.mobile2team.Data.assets.toFacilityDetail
import com.example.mobile2team.Data.model.FacilityDetail
import com.example.mobile2team.ViewModel.FacilityViewModel
import com.google.gson.Gson
import kotlin.collections.firstOrNull

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MapScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // 1. JSON 파싱
    val facilityDetails by remember {
        mutableStateOf(
            run {
                val jsonString = context.assets.open("facility.json").bufferedReader().use { it.readText() }
                val gson = Gson()
                val vworldResponse = gson.fromJson(jsonString, com.example.mobile2team.Data.assets.VWorldResponse::class.java)
                vworldResponse
                    .response
                    .result
                    .featureCollection
                    .features
                    .map { it.toFacilityDetail() }
            }
        )
    }

    // 2. 기존 마커 표시 코드 (facilityDetails 사용)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(
            facilityDetails.firstOrNull()?.let { LatLng(it.latitude ?: 0.0, it.longitude ?: 0.0) }
                ?: LatLng(37.5408, 127.0793),
            13.0
        )
    }

    NaverMap(
        modifier = Modifier.fillMaxWidth(),
        cameraPositionState = cameraPositionState
    ) {
        facilityDetails.forEach { facility ->
            if (facility.latitude != null && facility.longitude != null) {
                Marker(
                    state = rememberMarkerState(position = LatLng(facility.latitude, facility.longitude)),
                    captionText = facility.name
                )
            }
        }
    }
}


