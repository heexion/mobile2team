package com.example.mobile2team.Screen

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
import androidx.compose.foundation.lazy.rememberLazyListState
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

        // 검색된 시설들의 카드 목록
        if (filteredFacilities.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredFacilities) { facility ->
                    Card(
                        modifier = Modifier
                            .width(280.dp)
                            .clickable {
                                selectedFacility.value = facility
                                onFacilitySelected(facility)
                                // 카드 클릭 시 해당 시설의 위치로 카메라 이동
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
