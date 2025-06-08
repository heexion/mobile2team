package com.example.mobile2team.Screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale

/**
 * 복지시설 상세 정보 화면 - 시설 상세 정보 표시, 즐겨찾기 기능, 전화 걸기 기능
 * 仅负责展示设施的详细信息面板
 */
@Composable
fun DetailScreen(
    facilityId: String,
    navController: NavController,
    viewModel: DetailScreenViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // 초기 데이터 로드 / 初始加载数据
    LaunchedEffect(facilityId) {
        viewModel.loadFacilityDetail(facilityId)
    }

    // 직접 정보 패널만 표시 / 直接显示信息面板
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        uiState.facility?.let { facility ->
            FacilityInfoPanel(
                facility = facility,
                onToggleFavorite = { viewModel.toggleFavorite() },
                onCallPhone = { phoneNumber -> makePhoneCall(context, phoneNumber) },
                navController = navController
            )
        }
    }
}

/**
 * 시설 정보 패널 - 복지시설 상세 정보를 표시하는 카드
 * 设施信息面板 - 显示福利设施详细信息的卡片
 */
@Composable
fun FacilityInfoPanel(
    facility: FacilityDetail,
    onToggleFavorite: () -> Unit,
    onCallPhone: (String) -> Unit,
    navController: NavController? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 왼쪽 정보 열 / 左侧信息列
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 시설 이름 / 设施名称
                    Text(
                        text = facility.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    // 주소 / 地址
                    Text(
                        text = facility.address,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    // 전화번호 / 电话号码
                    Text(
                        text = facility.phoneNumber ?: "전화번호 정보 없음",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    // 평균 평점 / 平均评分
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "평점",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFFFD700)  // 黄色
                        )
                        Text(
                            text = "${facility.averageRating ?: 0.0f}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    // 리뷰 개수와 리뷰 보기 버튼 / 评论数和查看评论按钮
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${facility.reviewCount}개 리뷰",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )

                        TextButton(
                            onClick = {
                                navController?.navigate("review/${facility.id}")
                            },
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            enabled = navController != null
                        ) {
                            Text(
                                text = "리뷰 보기",
                                fontSize = 12.sp,
                                color = if (navController != null) Color.Blue else Color.Gray
                            )
                        }
                    }
                }

                // 오른쪽 이미지 영역 / 右侧图片区域
                Box(
                    modifier = Modifier
                        .size(width = 100.dp, height = 80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.image1),
                        contentDescription = "기관 사진",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 기능 버튼들 / 功能按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 즐겨찾기 버튼 / 收藏按钮
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (facility.isFavorite)
                                R.drawable.baseline_star_24
                            else
                                R.drawable.baseline_star_outline_24
                        ),
                        contentDescription = "즐겨찾기",
                        tint = if (facility.isFavorite) Color(0xFFFFD700) else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // 전화 버튼 / 电话按钮
                IconButton(
                    onClick = {
                        facility.phoneNumber?.let { onCallPhone(it) }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = "전화 걸기",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // 교통 정보 버튼 / 交通信息按钮
                IconButton(
                    onClick = { /* 교통 정보 */ },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_directions_bus_24),
                        contentDescription = "교통 정보",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
/**
 * 전화 걸기 기능 / 拨打电话功能
 */
fun makePhoneCall(context: Context, phoneNumber: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = "tel:$phoneNumber".toUri()
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}