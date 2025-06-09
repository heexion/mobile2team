package com.example.mobile2team.Data.repository

import com.example.mobile2team.Data.model.FacilityDetail

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

    private val favoriteIds = mutableSetOf<String>()
    private val userFavorites = mutableMapOf<String, MutableSet<String>>()

    // 👇 외부에서 받아오는 실제 데이터 목록
    private val facilityList = mutableListOf<FacilityDetail>()

    /** 외부에서 시설 리스트 주입하는 함수 */
    fun setFacilityList(newList: List<FacilityDetail>) {
        facilityList.clear()
        facilityList.addAll(newList)
    }

    /** 전체 시설 리스트 반환 */
    fun getAllFacilities(): List<FacilityDetail> {
        return facilityList
    }

    /** ID로 시설 찾기 */
    suspend fun getFacilityDetail(facilityId: String): Result<FacilityDetail> {
        return try {
            val facility = facilityList.find { it.id == facilityId }
                ?: throw IllegalArgumentException("시설을 찾을 수 없습니다.")
            val isFav = favoriteIds.contains(facilityId) || userFavorites["defaultUser"]?.contains(facilityId) == true
            Result.success(facility.copy(isFavorite = isFav))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleFavorite(facilityId: String): Result<Boolean> {
        return try {
            val newStatus = if (favoriteIds.contains(facilityId)) {
                favoriteIds.remove(facilityId)
                userFavorites["defaultUser"]?.remove(facilityId)
                false
            } else {
                favoriteIds.add(facilityId)
                userFavorites.getOrPut("defaultUser") { mutableSetOf() }.add(facilityId)
                true
            }
            Result.success(newStatus)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFavorites(): Result<List<FacilityDetail>> {
        return try {
            val favoriteList = favoriteIds.mapNotNull { id ->
                facilityList.find { it.id == id }?.copy(isFavorite = true)
            }
            Result.success(favoriteList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addUserFavorite(userId: String, facilityId: String): Result<Boolean> {
        return try {
            val userFavoriteSet = userFavorites.getOrPut(userId) { mutableSetOf() }
            userFavoriteSet.add(facilityId)
            favoriteIds.add(facilityId)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeUserFavorite(userId: String, facilityId: String): Result<Boolean> {
        return try {
            userFavorites[userId]?.remove(facilityId)
            favoriteIds.remove(facilityId)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserFavorites(userId: String): Result<List<FacilityDetail>> {
        return try {
            val userFavoriteIds = userFavorites[userId] ?: emptySet()
            val favoriteList = userFavoriteIds.mapNotNull { id ->
                facilityList.find { it.id == id }?.copy(isFavorite = true)
            }
            Result.success(favoriteList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
