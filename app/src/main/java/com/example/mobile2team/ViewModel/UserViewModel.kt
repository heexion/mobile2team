package com.example.mobile2team.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile2team.Data.model.User
import com.google.firebase.database.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel : ViewModel() {
    var name by mutableStateOf("")           // 사용자 이름
    var id by mutableStateOf("")             // 사용자 아이디
    var password by mutableStateOf("")       // 사용자 비밀번호
    var isLoggedIn by mutableStateOf(false)  // 로그인 상태
    var currentUserId by mutableStateOf("")  // 현재 로그인한 사용자의 고유 ID
    var errorMessage by mutableStateOf<String?>(null)

    var favorites by mutableStateOf<Map<String, Boolean>>(emptyMap())

    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    // 회원가입 - Firebase에 사용자 정보 저장
    fun registerUser(userName: String, userId: String, userPassword: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                // 아이디 중복 확인
                val snapshot = usersRef.orderByChild("id").equalTo(userId).get().await()
                if (snapshot.exists()) {
                    onResult(false, "이미 사용 중인 아이디입니다.")
                    return@launch
                }

                // 새 사용자 생성
                val newUserId = usersRef.push().key ?: return@launch
                val user = User(
                    id = userId,
                    name = userName,
                    password = userPassword, // 실제 앱에서는 해시화 필요
                    favorites = emptyMap()
                )

                usersRef.child(newUserId).setValue(user).await()
                onResult(true, "회원가입이 완료되었습니다.")
                
            } catch (e: Exception) {
                onResult(false, "회원가입 중 오류가 발생했습니다: ${e.message}")
            }
        }
    }

    // 로그인 - Firebase에서 사용자 정보 확인 및 불러오기
    fun loginUser(inputId: String, inputPassword: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val snapshot = usersRef.orderByChild("id").equalTo(inputId).get().await()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        if (user != null && user.password == inputPassword) {
                            currentUserId = userSnapshot.key ?: ""
                            name = user.name
                            id = user.id
                            password = user.password
                            isLoggedIn = true
                            // 즐겨찾기 목록 동기화
                            getUserFavorites { ids ->
                                val newFavorites = ids.associateWith { true }
                                favorites = newFavorites
                            }
                            onResult(true, "로그인 성공")
                            return@launch
                        }
                    }
                }
                onResult(false, "아이디 또는 비밀번호가 올바르지 않습니다.")
            } catch (e: Exception) {
                onResult(false, "로그인 중 오류가 발생했습니다: ${e.message}")
            }
        }
    }

    // 로그아웃
    fun logout() {
        name = ""
        id = ""
        password = ""
        currentUserId = ""
        isLoggedIn = false
        errorMessage = null
    }

    // 사용자의 즐겨찾기 목록 가져오기
    fun getUserFavorites(onResult: (List<String>) -> Unit) {
        if (currentUserId.isEmpty()) {
            onResult(emptyList())
            return
        }

        usersRef.child(currentUserId).child("favorites").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favorites = mutableListOf<String>()
                for (favoriteSnapshot in snapshot.children) {
                    val safeFacilityId = favoriteSnapshot.key ?: continue
                    val isFavorite = favoriteSnapshot.getValue(Boolean::class.java) ?: false
                    if (isFavorite) {
                        val facilityId = decodeFacilityId(safeFacilityId)
                        favorites.add(facilityId)
                    }
                }
                onResult(favorites)
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(emptyList())
            }
        })
    }

    // Firebase 경로에 사용할 facilityId의 특수문자 치환
    private fun encodeFacilityId(facilityId: String): String {
        return facilityId.replace(".", ",")
            .replace("#", ",")
            .replace("$", ",")
            .replace("[", ",")
            .replace("]", ",")
    }

    // DB에서 읽어온 safeFacilityId를 원래 facilityId로 복원
    private fun decodeFacilityId(safeFacilityId: String): String {
        return safeFacilityId.replace(",", ".")
        // 필요하다면 다른 특수문자도 역치환
    }

    // 즐겨찾기 추가/제거
    fun toggleFavorite(facilityId: String, onResult: (Boolean) -> Unit) {
        val safeFacilityId = encodeFacilityId(facilityId)
        Log.d("Favorite", "toggleFavorite called: facilityId=$facilityId, safeFacilityId=$safeFacilityId, currentUserId=$currentUserId")
        if (currentUserId.isEmpty()) {
            Log.e("Favorite", "currentUserId is empty! 로그인 필요")
            onResult(false)
            return
        }

        viewModelScope.launch {
            try {
                val favoriteRef = usersRef.child(currentUserId).child("favorites").child(safeFacilityId)
                val snapshot = favoriteRef.get().await()
                
                val currentState = snapshot.getValue(Boolean::class.java) ?: false
                val newState = !currentState
                
                favoriteRef.setValue(newState).await()
                // 상태도 즉시 갱신 (원래 facilityId로 관리)
                favorites = favorites.toMutableMap().apply { put(facilityId, newState) }
                Log.d("Favorite", "즐겨찾기 상태 변경: $facilityId -> $newState (currentUserId=$currentUserId)")
                onResult(newState)
                
            } catch (e: Exception) {
                Log.e("Favorite", "toggleFavorite error: ${e.message}")
                onResult(false)
            }
        }
    }

    // 회원가입 시 호출 → 사용자 정보 저장
    fun setUserInfo(userName: String, userId: String, userPw: String) {
        name = userName
        id = userId
        password = userPw
    }

    fun checkLogin(inputId: String, inputPw: String): Boolean {
        val success = inputId == id && inputPw == password
        isLoggedIn = success
        return success
    }
}
