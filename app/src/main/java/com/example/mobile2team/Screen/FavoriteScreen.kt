package com.example.mobile2team.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mobile2team.Data.model.FacilityDetail
import com.example.mobile2team.ViewModel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    allFacilities: List<FacilityDetail>
) {
    // 1. 즐겨찾기된 facilityId만 추출
    val favoriteIds = userViewModel.favorites.filterValues { it }.keys

    // 2. 즐겨찾기된 시설만 리스트로 추출
    val favoriteFacilities = allFacilities.filter { it.id in favoriteIds }

    // 3. UI에 favoriteFacilities를 사용
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "즐겨찾기 목록",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로 가기")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (favoriteFacilities.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("즐겨찾기한 시설이 없습니다.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoriteFacilities) { facility ->
                    FavoriteItem(
                        facility = facility,
                        isEditMode = false,
                        isSelected = false,
                        onItemClick = { navController.navigate("detail/${facility.id}") }
                    )
                }
            }
        }
    }
}

/**
 * 즐겨찾기 항목 컴포저블
 */
@Composable
private fun FavoriteItem(
    facility: FacilityDetail,
    isEditMode: Boolean,
    isSelected: Boolean,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        onClick = onItemClick,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = facility.name,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                    maxLines = 1
                )
                Text(
                    text = facility.address,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

