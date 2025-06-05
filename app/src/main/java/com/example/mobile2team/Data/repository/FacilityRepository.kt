package com.example.mobile2team.Data.repository

import com.example.mobile2team.Data.model.FacilityDetail
import com.example.mobile2team.Data.model.Review
import kotlinx.coroutines.delay

/**
 * 복지시설 데이터 저장소 - DetailScreen에 필요한 데이터 제공 및 즐겨찾기 상태관리
 * 福利设施数据仓库，为DetailScreen提供所需数据并管理收藏状态
 */
class FacilityRepository private constructor() {

    companion object {
        @Volatile
        private var INSTANCE: FacilityRepository? = null

        fun getInstance(): FacilityRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FacilityRepository().also { INSTANCE = it }
            }
        }
    }

    // 사용자가 즐겨찾기한 복지시설의 ID를 저장 / 记录用户收藏的福利设施ID
    private val favoriteIds = mutableSetOf<Long>()

    // 사용자별 즐겨찾기 저장 / 按用户存储收藏
    private val userFavorites = mutableMapOf<String, MutableSet<Long>>()

    // Mock 리뷰 데이터 저장 / 存储模拟评论数据
    private val reviewsMap = mutableMapOf<Long, MutableList<Review>>()

    init {
        // 테스트용 리뷰 데이터 초기화 / 初始化测试评论数据
        initMockReviews()
    }

    /**
     * 테스트용 함수
     * DetailScreen화면이 정상 작동하는지 확인하기 위해 가짜 데이터를 반환합니다
     * 추후 실제 API와 연동되면 이함수를 수정하거나 대체할 예정입니다
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
     * 즐겨찾기 상태를 서버와 동기화해야 하는 경우, 코드를 수정해야합니다
     */
    suspend fun toggleFavorite(facilityId: Long): Result<Boolean> {
        return try {
            val newStatus = if (favoriteIds.contains(facilityId)) {
                favoriteIds.remove(facilityId)
                // 동시에 사용자 즐겨찾기에서도 제거 / 同时从用户收藏中移除
                userFavorites["defaultUser"]?.remove(facilityId)
                false
            } else {
                favoriteIds.add(facilityId)
                // 동시에 사용자 즐겨찾기에도 추가 / 同时添加到用户收藏
                userFavorites.getOrPut("defaultUser") { mutableSetOf() }.add(facilityId)
                true
            }
            Result.success(newStatus)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 즐겨찾기된 복지시설 목록을 가져옵니다 / 获取收藏列表
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
     * 사용자별 즐겨찾기 추가 / 为特定用户添加收藏
     */
    suspend fun addUserFavorite(userId: String, facilityId: Long): Result<Boolean> {
        return try {
            val userFavoriteSet = userFavorites.getOrPut(userId) { mutableSetOf() }
            userFavoriteSet.add(facilityId)

            // favoriteIds에도 추가하여 동기화 / 同步到favoriteIds
            favoriteIds.add(facilityId)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 사용자별 즐겨찾기 제거 / 为特定用户移除收藏
     */
    suspend fun removeUserFavorite(userId: String, facilityId: Long): Result<Boolean> {
        return try {
            val userFavoriteSet = userFavorites[userId]
            userFavoriteSet?.remove(facilityId)

            // favoriteIds에서도 제거하여 동기화 / 同步从favoriteIds移除
            favoriteIds.remove(facilityId)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 사용자별 즐겨찾기 목록 조회 / 获取特定用户的收藏列表
     */
    suspend fun getUserFavorites(userId: String): Result<List<FacilityDetail>> {
        return try {
            val userFavoriteIds = userFavorites[userId] ?: emptySet()
            val favoriteList = userFavoriteIds.map { id ->
                createMockFacility(id).copy(isFavorite = true)
            }

            Result.success(favoriteList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 특정 시설의 리뷰 목록 조회 / 获取特定设施的评论列表
     */
    suspend fun getReviews(facilityId: Long): Result<List<Review>> {
        return try {
            val reviews = reviewsMap[facilityId] ?: emptyList()
            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mock 리뷰 데이터 초기화 / 初始化模拟评论数据
     */
    private fun initMockReviews() {
        for (facilityId in 1L..5L) {
            val reviews = mutableListOf<Review>()

            reviews.add(Review(
                userId = "user1",
                content = "***********",
                rating = 5
            ))

            reviews.add(Review(
                userId = "user2",
                content = "***********",
                rating = 4
            ))

            reviews.add(Review(
                userId = "user3",
                content = "***********",
                rating = 3
            ))

            reviewsMap[facilityId] = reviews
        }
    }

    /**
     * Mock 데이터 생성 함수 / 创建模拟数据的函数
     */
    private fun createMockFacility(id: Long): FacilityDetail {
        // 다양한 시설 이름 / 多样化的设施名称
        val facilityNames = listOf(
            "복지시설1",
            "복지시설2",
            "복지시설3",
            "복지시설4",
            "복지시설5"
        )

        val index = (id.toInt() - 1) % facilityNames.size

        return FacilityDetail(
            id = id,
            name = facilityNames[index],
            address = "서울특별시 광진구 능동로",
            phoneNumber = "010-1234-5678",
            latitude = 37.5172,
            longitude = 127.0473,
            operatingHours = "평일 09:00-18:00",
            averageRating = 4.2f,
            reviewCount = reviewsMap[id]?.size ?: 0,  // 실제 리뷰 개수 반영
            imageUrl = "https://example.com/facility$id.jpg",
            isFavorite = favoriteIds.contains(id) || userFavorites["defaultUser"]?.contains(id) == true
        )
    }
}