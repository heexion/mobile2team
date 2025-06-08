package com.example.mobile2team.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mobile2team.Data.model.FacilityDetail
import com.example.mobile2team.ViewModel.FavoriteViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

/**
 * 즐겨찾기 목록 화면，사용자가 즐겨찾기한 복지시설 목록을 표시하고 관리할 수 있습니다
 * 收藏列表页面，显示和管理用户收藏的福利设施列表
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    navController: NavHostController,
    viewModel: FavoriteViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isEditMode by remember { mutableStateOf(false) }
    var selectedItems by remember { mutableStateOf(setOf<String>()) }  // 改为 String 类型
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "즐겨찾기 목록",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로 가기"
                        )
                    }
                },
                actions = {
                    // 편집 모드 토글 버튼 (휴지통 아이콘) / 编辑模式切换按钮（垃圾桶图标）
                    IconButton(
                        onClick = {
                            isEditMode = !isEditMode
                            if (!isEditMode) {
                                selectedItems = emptySet()
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = if (isEditMode) "편집 완료" else "편집",
                            tint = if (isEditMode) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // 편집 모드일 때만 하단 바 표시 / 仅在编辑模式下显示底部栏
            if (isEditMode && uiState.favorites.isNotEmpty()) {
                BottomAppBar(
                    modifier = Modifier.height(70.dp),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 전체 선택 버튼 / 全选按钮
                        TextButton(
                            onClick = {
                                selectedItems = if (selectedItems.size == uiState.favorites.size) {
                                    emptySet()
                                } else {
                                    uiState.favorites.map { it.id }.toSet()  // id 已经是 String
                                }
                            }
                        ) {
                            Text(
                                text = if (selectedItems.size == uiState.favorites.size)
                                    "전체 해제" else "전체 선택",
                                fontSize = 16.sp
                            )
                        }

                        // 삭제 버튼 / 删除按钮
                        Button(
                            onClick = {
                                if (selectedItems.isNotEmpty()) {
                                    showDeleteDialog = true
                                }
                            },
                            enabled = selectedItems.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                disabledContainerColor = Color.Gray
                            )
                        ) {
                            Text(
                                text = "삭제 (${selectedItems.size})",
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(uiState.isRefreshing),
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.favorites.isEmpty() -> {
                    EmptyFavoritesContent()
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.favorites) { facility ->
                            FavoriteItem(
                                facility = facility,
                                isEditMode = isEditMode,
                                isSelected = selectedItems.contains(facility.id),
                                onItemClick = {
                                    if (isEditMode) {
                                        selectedItems = if (selectedItems.contains(facility.id)) {
                                            selectedItems - facility.id
                                        } else {
                                            selectedItems + facility.id
                                        }
                                    } else {
                                        navController.navigate("detail/${facility.id}")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // 삭제 확인 다이얼로그 / 删除确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("즐겨찾기 삭제") },
            text = {
                Text("선택한 ${selectedItems.size}개 항목을 즐겨찾기에서 삭제하시겠습니까?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedItems.forEach { id ->
                            viewModel.removeFavorite(id)  // id 现在是 String
                        }
                        selectedItems = emptySet()
                        isEditMode = false
                        showDeleteDialog = false
                    }
                ) {
                    Text("삭제", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("취소")
                }
            }
        )
    }
}

/**
 * 즐겨찾기 항목 컴포저블
 * 收藏项目组件
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 체크박스 / 复选框
            if (isEditMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = null,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = facility.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Text(
                        text = facility.address,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFFFFD700)
                    )
                    Text(
                        text = "${facility.averageRating ?: 0.0f} (${facility.reviewCount})",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            if (!isEditMode) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "위치",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Gray
                )
            }
        }
    }
}

/**
 * 빈 즐겨찾기 목록 표시
 * 空收藏列表显示
 */
@Composable
private fun EmptyFavoritesContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Text(
                text = "즐겨찾기한 시설이 없습니다",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Text(
                text = "관심있는 복지시설을 즐겨찾기에 추가해보세요",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}