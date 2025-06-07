package com.example.mobile2team.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile2team.Data.model.FacilityDetail
import com.example.mobile2team.Data.repository.FacilityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 즐겨찾기 목록 화면용 ViewModel
 * 收藏列表页面的 ViewModel
 *
 * 사용자의 즐겨찾기 목록을 관리하고 삭제 기능을 제공합니다
 * 管理用户的收藏列表并提供删除功能
 */
class FavoriteViewModel : ViewModel() {

    private val repository = FacilityRepository.getInstance()

    // 현재 사용자 ID (임시로 기본값 사용) / 当前用户ID（暂时使用默认值）
    private val currentUserId = "defaultUser"

    /**
     * UI 상태 데이터 클래스 / UI状态数据类
     */
    data class UiState(
        val favorites: List<FacilityDetail> = emptyList(),  // 즐겨찾기 목록 / 收藏列表
        val isLoading: Boolean = false,                     // 로딩 상태 / 加载状态
        val error: String? = null,                          // 에러 메시지 / 错误信息
        val isRefreshing: Boolean = false                   // 새로고침 상태 / 刷新状态
    )

    // 내부 상태 / 内部状态
    private val _uiState = MutableStateFlow(UiState())

    // 외부 공개 상태 / 对外公开的状态
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        // 초기 데이터 로드 / 初始加载数据
        loadFavorites()
    }

    /**
     * 즐겨찾기 목록 불러오기 / 加载收藏列表
     */
    fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            repository.getUserFavorites(currentUserId)
                .onSuccess { favorites ->
                    _uiState.value = _uiState.value.copy(
                        favorites = favorites,
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "즐겨찾기를 불러올 수 없습니다: ${exception.message}"
                    )
                }
        }
    }

    /**
     * 즐겨찾기 삭제 / 删除收藏
     */
    fun removeFavorite(facilityId: Long) {
        viewModelScope.launch {
            repository.removeUserFavorite(currentUserId, facilityId)
                .onSuccess {
                    // 삭제 성공 시 목록에서 제거 / 删除成功后从列表中移除
                    _uiState.value = _uiState.value.copy(
                        favorites = _uiState.value.favorites.filter { it.id != facilityId.toString() }
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = "삭제에 실패했습니다: ${exception.message}"
                    )
                }
        }
    }

    /**
     * 새로고침 / 刷新
     */
    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)

            repository.getUserFavorites(currentUserId)
                .onSuccess { favorites ->
                    _uiState.value = _uiState.value.copy(
                        favorites = favorites,
                        isRefreshing = false,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        error = "새로고침 실패: ${exception.message}"
                    )
                }
        }
    }
}