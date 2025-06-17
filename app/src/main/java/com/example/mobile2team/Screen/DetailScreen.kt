package com.example.mobile2team.Screen

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobile2team.Data.model.FacilityDetail
import com.example.mobile2team.R
import com.example.mobile2team.Util.FacilityInfoPanel
import com.example.mobile2team.ViewModel.DetailScreenViewModel
import com.example.mobile2team.ViewModel.UserViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    facilityId: String,
    navController: NavController,
    userViewModel: UserViewModel,
    viewModel: DetailScreenViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val favorites by remember { userViewModel::favorites }

    LaunchedEffect(facilityId) {
        viewModel.loadFacilityDetail(facilityId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("시설 상세 정보") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "뒤로가기"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            uiState.facility?.let { facility ->
                val isFavorite = favorites[facility.id] == true
                val isLoggedIn = userViewModel.isLoggedIn
                FacilityInfoPanel(
                    facility = facility.copy(isFavorite = isFavorite),
                    onToggleFavorite = {
                        userViewModel.toggleFavorite(facility.id) { }
                    },
                    onCallPhone = { phoneNumber -> makePhoneCall(context, phoneNumber) },
                    navController = navController,
                    isLoggedIn = isLoggedIn
                )
            }
        }
    }
}

/**
 * 시설 정보 패널 - 복지시설 상세 정보를 표시하는 카드
 */
@Composable
fun FacilityInfoPanel(
    facility: FacilityDetail,
    onToggleFavorite: () -> Unit,
    onCallPhone: (String) -> Unit,
    navController: NavController? = null,
    isLoggedIn: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .background(Color.White)
        ) {
            // 정보 표시 Row
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(facility.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(facility.address, fontSize = 16.sp, color = Color.DarkGray)
                    Text(
                        facility.phoneNumber ?: "전화번호 정보 없음",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700))
                        Text("${facility.averageRating ?: 0.0f}", fontSize = 16.sp)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("${facility.reviewCount}개 리뷰", fontSize = 16.sp)
                        TextButton(onClick = {
                            navController?.navigate("review/${facility.id}")
                        }) {
                            Text("리뷰 보기", fontSize = 14.sp, color = Color.Blue)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(width = 120.dp, height = 100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.image1),
                        contentDescription = "기관 사진",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 기능 버튼 Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = {
                    if (isLoggedIn) {
                        onToggleFavorite()
                    } else {
                        android.widget.Toast.makeText(context, "로그인을 해주세요", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Icon(
                        painter = painterResource(
                            id = if (facility.isFavorite)
                                R.drawable.baseline_star_24
                            else
                                R.drawable.baseline_star_outline_24
                        ),
                        contentDescription = "즐겨찾기",
                        tint = if (facility.isFavorite) Color(0xFFFFD700) else Color.Gray
                    )
                }

                IconButton(onClick = {
                    facility.phoneNumber?.let { onCallPhone(it) }
                }) {
                    Icon(Icons.Default.Phone, contentDescription = "전화", tint = Color.Gray)
                }

                IconButton(onClick = {
                    navController?.navigate("route/${facility.latitude}/${facility.longitude}/${facility.name}")
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_directions_bus_24),
                        contentDescription = "교통 정보",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

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
