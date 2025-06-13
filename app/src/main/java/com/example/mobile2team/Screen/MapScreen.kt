package com.example.mobile2team.Screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.mobile2team.Data.assets.toFacilityDetail
import com.example.mobile2team.Data.model.FacilityDetail
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberMarkerState
import com.naver.maps.map.compose.MapUiSettings
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

    // JSON 파싱
    val facilityDetails = remember {
        val jsonString = context.assets.open("facility.json").bufferedReader().use { it.readText() }
        val gson = Gson()
        val vworldResponse = gson.fromJson(jsonString, com.example.mobile2team.Data.assets.VWorldResponse::class.java)
        mutableStateOf(
            vworldResponse.response.result.featureCollection.features.map { it.toFacilityDetail() }
        )
    }

    // 검색어에 따른 필터링
    val filteredFacilities = facilityDetails.value.filter {
        it.name?.contains(searchQuery, ignoreCase = true) == true
    }

    // 지도 초기 위치
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(
            filteredFacilities.firstOrNull()?.let { LatLng(it.latitude ?: 0.0, it.longitude ?: 0.0) }
                ?: LatLng(37.5408, 127.0793),
            13.0
        )
    }

    // 검색어 변경 시 가장 가까운 시설로 이동 + 선택
    LaunchedEffect(searchQuery) {
        val baseLocation = cameraPositionState.position.target
        val closestMatch = filteredFacilities
            .filter { it.latitude != null && it.longitude != null }
            .minByOrNull { facility ->
                val facilityLatLng = LatLng(facility.latitude!!, facility.longitude!!)
                baseLocation.distanceTo(facilityLatLng)
            }

        if (closestMatch != null) {
            coroutineScope.launch {
                cameraPositionState.move(
                    CameraUpdate.toCameraPosition(
                        CameraPosition(
                            LatLng(closestMatch.latitude!!, closestMatch.longitude!!),
                            15.0
                        )
                    )
                )
                selectedFacility.value = closestMatch
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        NaverMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                isScrollGesturesEnabled = true
            )
        ) {
            filteredFacilities.forEach { facility ->
                if (facility.latitude != null && facility.longitude != null) {
                    Marker(
                        state = rememberMarkerState(
                            position = LatLng(facility.latitude!!, facility.longitude!!)
                        ),
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

        // 선택된 시설이 있을 경우 상세 정보 패널 표시 (맵 내부)
//        selectedFacility.value?.let { facility ->
//            FacilityInfoPanel(
//                facility = facility,
//                onToggleFavorite = { /* 즐겨찾기 기능 */ },
//                onCallPhone = { phoneNumber -> makePhoneCall(context, phoneNumber) },
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .padding(bottom = 24.dp)
//            )
//        }
    }
}
