package com.example.mobile2team.Data.model

data class User(
    val id: String = "",
    val name: String = "",
    val password: String = "", // 실제 앱에서는 해시화 필요
    val favorites: Map<String, Boolean> = emptyMap() // facilityId를 키로 하는 즐겨찾기 목록
) {
    
    constructor() : this("", "", "", emptyMap())
} 