package com.example.mobile2team.Data.model

/**
 * 복지시설의 데이터 클래스
 * 福利设施的数据类
 */

data class FacilityDetail(
    //외부 데이터 정보
    val id: String = "",                                // 시설 고유 ID /
    val name: String = "",                            // 시설 이름 /
    val address: String = "",                         // 상세 주소 /
    val phoneNumber: String? = null,             // 전화번호 /
    val latitude: Double? = null,                // 위도 /
    val longitude: Double? = null,               // 경도 /

    //우리 앱 자체 정보
    val averageRating: Float? = null,            // 평균 평점 (1-5점) /
    val reviewCount: Int = 0,                    // 리뷰 총 개수 /
    val imageUrl: String? = null,                // 시설 이미지 URL 목록 /
    var isFavorite: Boolean = false,             // 즐겨찾기 상태 /
    val reviews: Map<String, Review>? = null
)

/*
val operatingHours: String? = null,          // 운영 시간
 */