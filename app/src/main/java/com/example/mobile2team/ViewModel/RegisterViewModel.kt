package com.example.mobile2team.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    var name by mutableStateOf("")
    var id by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)

    fun registerUser() {
        if (name.isBlank() || id.isBlank() || password.length < 6) {
            errorMessage = "입력값을 확인하세요."
            return
        }
        if (password != confirmPassword) {
            errorMessage = "비밀번호가 일치하지 않습니다."
            return
        }

        viewModelScope.launch {
            try {
                //val result = api.register(name, id, password)
                // 성공 시 처리
            } catch (e: Exception) {
                errorMessage = "서버 오류: ${e.message}"
            }
        }
    }
}
