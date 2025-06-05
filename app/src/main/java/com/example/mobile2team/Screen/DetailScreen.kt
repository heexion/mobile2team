package com.example.mobile2team.Screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.mobile2team.Data.model.FacilityDetail
import com.example.mobile2team.R
import com.example.mobile2team.ViewModel.DetailScreenViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController

/**
 * 복지시설 상세 정보 화면 - 시설 상세 정보 표시, 즐겨찾기 기능, 전화 걸기 기능
 * 福利设施详细信息界面，显示设施详细信息，收藏功能，拨打电话功能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    facilityId: Long,
    navController: NavController,
    viewModel: DetailScreenViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showInfoPanel by remember { mutableStateOf(false) }

    //초기 데이터 로드 / 初始加载数据
    LaunchedEffect(facilityId) {
        viewModel.loadFacilityDetail(facilityId)
    }

    Scaffold(
        topBar = {
            // 상단 앱바 / 顶部应用栏
            TopAppBar(
                title = {
                    Text(
                        text = "주변 복지시설", // 周边福利设施
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* 뒤로가기 */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로 가기")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 메인 컨텐츠 / 主要内容
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // 검색 바 / 搜索栏
                SearchBarPlaceholder()

                Spacer(modifier = Modifier.height(16.dp))

                // 지도 영역 (클릭 가능) / 地图区域 (可点击)
                MapPlaceholder(
                    onMapClick = { showInfoPanel = !showInfoPanel },
                    modifier = Modifier.weight(1f) // 남은 공간 모두 차지
                )
            }

            // 정보 패널 (조건부 표시) / 信息面板 (条件显示)
            if (showInfoPanel && uiState.facility != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    FacilityInfoPanel(
                        facility = uiState.facility!!,
                        onToggleFavorite = { viewModel.toggleFavorite() },
                        onCallPhone = { phoneNumber ->
                            makePhoneCall(context, phoneNumber)
                        }
                    )
                }
            }
        }
    }
}

/**
 * 검색 바 플레이스홀더 / 搜索栏占位符
 */
@Composable
private fun SearchBarPlaceholder() {
    OutlinedTextField(
        value = "",
        onValueChange = { },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("검색") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "검색")
        },
        enabled = false
    )
}

/**
 * 지도 플레이스홀더 (클릭 가능) / 地图占位符 (可点击)
 */
@Composable
private fun MapPlaceholder(
    onMapClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onMapClick() }, // 클릭 이벤트 추가
        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "지도",
                    modifier = Modifier.size(48.dp),
                    tint = Color.Gray
                )
                Text(
                    text = "지도 화면",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/**
 * 시설 정보 패널 (하단에서 슬라이드업) / 设施信息面板 (从底部滑出)
 */
@Composable
private fun FacilityInfoPanel(
    facility: FacilityDetail,
    onToggleFavorite: () -> Unit,
    onCallPhone: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 시설 정보 (라벨 없이 직접 표시) / 设施信息 (无标签直接显示)
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 왼쪽 정보 열 / 左侧信息列
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 기관명 직접 표시 / 直接显示机构名
                    Text(
                        text = facility.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    // 운영시간 직접 표시 / 直接显示营业时间
                    Text(
                        text = facility.operatingHours ?: "운영시간 정보 없음",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    // 전화번호 직접 표시 / 直接显示电话号码
                    Text(
                        text = facility.phoneNumber ?: "전화번호 정보 없음",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    // 리뷰 개수 직접 표시 / 直接显示评价数量
                    Text(
                        text = "${facility.reviewCount}개 리뷰",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    // 빈 공간 또는 추가 정보 / 空白或额外信息
                    Text(
                        text = "",
                        fontSize = 14.sp
                    )
                }

                // 오른쪽 이미지 영역 / 右侧图片区域
                Box(
                    modifier = Modifier
                        .size(width = 150.dp, height = 110.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "기관 사진",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 기능 버튼들 / 功能按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 즐겨찾기 버튼 / 收藏按钮
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        painter = painterResource(
                            id = if (facility.isFavorite)
                                R.drawable.baseline_star_24
                            else
                                R.drawable.baseline_star_outline_24
                        ),
                        contentDescription = "즐겨찾기",
                        tint = if (facility.isFavorite) Color(0xFFFFD700) else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // 전화 버튼 / 电话按钮
                IconButton(
                    onClick = {
                        facility.phoneNumber?.let { onCallPhone(it) }
                    }
                ) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = "전화 걸기",
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // 교통 정보 버튼 / 交通信息按钮
                IconButton(onClick = { /* 교통 정보 */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_directions_bus_24),
                        contentDescription = "교통 정보",
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

/**
 * 전화 걸기 기능 / 拨打电话功能
 */
private fun makePhoneCall(context: Context, phoneNumber: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = "tel:$phoneNumber".toUri()
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}