package com.example.mobile2team.ViewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile2team.Data.model.FacilityDetail
import com.example.mobile2team.Data.repository.FacilityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class DetailScreenViewModel : ViewModel() {


    private val repository = FacilityRepository.getInstance()


    data class UiState(
        val facility: FacilityDetail? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    // 내부에서 UI 상태를 업데이터하는 용도 /
    private val _uiState = MutableStateFlow(UiState())

    // 외부 UI에서 UI상태 변화를 관찰할 수 있도록 제공함
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()



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

}