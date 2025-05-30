package com.example.mobile2team.Data.model

/**
 * 복지시설의 데이터 클래스
 * 福利设施的数据类
 */

data class FacilityDetail(
    val id: Long,                                // 시설 고유 ID / 设施唯一ID
    val name: String,                            // 시설 이름 / 设施名称
    val address: String,                         // 상세 주소 / 详细地址
    val phoneNumber: String? = null,             // 전화번호 / 电话号码
    val latitude: Double? = null,                // 위도 / 纬度
    val longitude: Double? = null,               // 경도 / 经度
    val operatingHours: String? = null,          // 운영 시간 / 营业时间
    val averageRating: Float? = null,            // 평균 평점 (1-5점) / 平均评分
    val reviewCount: Int = 0,                    // 리뷰 총 개수 / 总评价数
    val imageUrl: String? = null,                // 시설 이미지 URL 목록 / 设施图片URL
    var isFavorite: Boolean = false,             // 즐겨찾기 상태 / 收藏状态
)