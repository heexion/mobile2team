package com.example.mobile2team.Data.repository

import com.example.mobile2team.Data.model.FacilityDetail
import kotlinx.coroutines.delay

/**
 * 복지시설 데이터 저장소 - DetailScreen에 필요한 데이터 제공 및 즐겨찾기 상태관리
 * 福利设施数据仓库，为DetailScreen提供所需数据并管理收藏状态
 */
class FacilityRepository {

    // 사용자가 즐겨찾기한 복지시설의 ID를 저장 / 记录用户收藏的福利设施ID
    private val favoriteIds = mutableSetOf<Long>()

    /**
     * 테스트용 함수
     * DetailScreen화면이 정상 작동하는지 확인하기 위해 가짜 데이터를 반환힙나다
     * 추후 실제 API와 연동되면 이함수를 수정하거나 대채할 예정입니다
     *
     * 测试函数（API连接后替换）
     */
    suspend fun getFacilityDetail(facilityId: Long): Result<FacilityDetail> {
        return try {
            delay(1000)
            val facility = createMockFacility(facilityId)
            Result.success(facility)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 즐겨찾기 상태 토글 / 切换收藏状态
     * 즐겨찾기 상태를 서버와 동기화해야 하는 겨우, 코드를 수정해야합니다
     */
    suspend fun toggleFavorite(facilityId: Long): Result<Boolean> {
        return try {
            val newStatus = if (favoriteIds.contains(facilityId)) {
                favoriteIds.remove(facilityId)
                false
            } else {
                favoriteIds.add(facilityId)
                true
            }
            Result.success(newStatus)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 즐겨찾기된 복지시설 목록을 자져옵니다 / 获取收藏列表
     * 서버와 동기화가 필요하다면 코드를 수정해야 합니다（API연동이 필요합니다）
     */
    suspend fun getFavorites(): Result<List<FacilityDetail>> {
        return try {
            val favoriteList = favoriteIds.map { id ->
                createMockFacility(id).copy(isFavorite = true)
            }
            Result.success(favoriteList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mock 데이터 생성 함수 / 创建模拟数据的函数
     */
    private fun createMockFacility(id: Long): FacilityDetail {
        return FacilityDetail(
            id = id,
            name = "강남구청 복지관 $id",                  // 시설 이름 / 设施名称
            address = "서울특별시 강남구 학동로 426",      // 상세 주소 / 详细地址
            phoneNumber = "02-3423-5000",               // 전화번호 / 电话号码
            latitude = 37.5172,                         // 위도 / 纬度
            longitude = 127.0473,                       // 경도 / 经度
            operatingHours = "평일 09:00-18:00",        // 운영 시간 / 营业时间
            averageRating = 4.2f,                       // 평균 평점 / 平均评分
            reviewCount = 23,                           // 리뷰 총 개수 / 总评价数
            imageUrl = "https://example.com/facility$id.jpg", // 시설 이미지 URL / 设施图片URL
            isFavorite = favoriteIds.contains(id)       // 즐겨찾기 상태 / 收藏状态
        )
    }
}