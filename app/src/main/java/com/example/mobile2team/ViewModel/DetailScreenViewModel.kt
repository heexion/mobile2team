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
 * DetailScreen용 ViewModel - 시설 상세 정보 데이터 관리，즐겨찾기 상태 관리，UI 상태 관리
 * 管理设施详细信息数据，管理收藏状态，管理UI状态
 */
class DetailScreenViewModel : ViewModel() {

    /**
     * 시설 정보를 가져오고 즐겨찾기 상태를 관리합니다(현재는 모의 데이터입니다)
     * 获取设施信息和管理收藏状态（模拟数据）
     */
    private val repository = FacilityRepository.getInstance()

    /**
     * FacilityRepository인스턴스를 생성합니다(모의 데이터)
     * 创建FacilityRepository实例（模拟数据）
     */
    data class UiState(
        val facility: FacilityDetail? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    // 내부에서 UI 상태를 업데이터하는 용도 / 用于内部更新UI状态
    private val _uiState = MutableStateFlow(UiState())

    // 외부 UI에서 UI상태 변화를 관찰할 수 있도록 제공함 / 供外部UI观察UI状态变化
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /**
     * 지정된 ID의 복지시설 상세 정보를 불러오고 UI상태를 업데이트합니다
     * 加载指定ID的福利设施详细信息，并更新UI状态
     */
    fun loadFacilityDetail(facilityId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            repository.getFacilityDetail(facilityId)
                .onSuccess { facility ->
                    _uiState.value = _uiState.value.copy(
                        facility = facility,
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "데이터를 불러올 수 없습니다"
                    )
                }
        }
    }

    fun loadFacilityDetailFromList(id: String, allFacilities: List<FacilityDetail>) {
        viewModelScope.launch {
            val facility = allFacilities.find { it.id == id }
            _uiState.value = _uiState.value.copy(facility = facility)
        }
    }

    // 즐겨찾기 추가 또는 제거 / 添加或取消收藏
    fun toggleFavorite() {
        val currentFacility = _uiState.value.facility ?: return
        viewModelScope.launch {
            repository.toggleFavorite(currentFacility.id)
                .onSuccess { newFavoriteStatus ->
                    _uiState.value = _uiState.value.copy(
                        facility = currentFacility.copy(isFavorite = newFavoriteStatus)
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        error = "즐겨찾기 설정에 실패했습니다"
                    )
                }
        }
    }
}