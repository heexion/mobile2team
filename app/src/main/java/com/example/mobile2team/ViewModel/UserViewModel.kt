package com.example.mobile2team.ViewModel

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
                            // 즐겨찾기 정보도 함께 읽어오기
                            val favoritesSnapshot = userSnapshot.child("favorites")
                            val favoritesMap = mutableMapOf<String, Boolean>()
                            for (favorite in favoritesSnapshot.children) {
                                val facilityId = favorite.key
                                val isFavorite = favorite.getValue(Boolean::class.java) ?: false
                                if (facilityId != null) {
                                    favoritesMap[facilityId] = isFavorite
                                }
                            }
                            // 로그인 성공
                            currentUserId = userSnapshot.key ?: ""
                            name = user.name
                            id = user.id
                            password = user.password
                            isLoggedIn = true
                            // User 객체의 favorites 필드에 값 할당
                            val userWithFavorites = user.copy(favorites = favoritesMap)
                            // 필요하다면 userWithFavorites를 상태로 저장하거나 반환
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
                    val facilityId = favoriteSnapshot.key
                    val isFavorite = favoriteSnapshot.getValue(Boolean::class.java) ?: false
                    if (isFavorite && facilityId != null) {
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

    // 즐겨찾기 추가/제거
    fun toggleFavorite(facilityId: String, onResult: (Boolean) -> Unit) {
        if (currentUserId.isEmpty()) {
            onResult(false)
            return
        }

        viewModelScope.launch {
            try {
                val favoriteRef = usersRef.child(currentUserId).child("favorites").child(facilityId)
                val snapshot = favoriteRef.get().await()
                
                val currentState = snapshot.getValue(Boolean::class.java) ?: false
                val newState = !currentState
                
                favoriteRef.setValue(newState).await()
                onResult(newState)
                
            } catch (e: Exception) {
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
