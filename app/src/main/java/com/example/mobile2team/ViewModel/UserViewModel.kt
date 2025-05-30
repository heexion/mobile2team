package com.example.mobile2team.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    var name by mutableStateOf("")           // 사용자 이름
    var id by mutableStateOf("")             // 사용자 아이디
    var password by mutableStateOf("")       // 사용자 비밀번호
    var isLoggedIn by mutableStateOf(false) // ✅ 로그인 상태 추가


    // 회원가입 시 호출 → 사용자 정보 저장
    fun setUserInfo(userName: String, userId: String, userPw: String) {
        name = userName
        id = userId
        password = userPw
    }

    // 로그인 시 호출 → 입력한 아이디와 비밀번호가 저장된 값과 일치하는지 확인
    fun checkLogin(inputId: String, inputPw: String): Boolean {
        val success = inputId == id && inputPw == password
        isLoggedIn = success
        return success
    }
}
