package com.example.mobile2team.ViewModel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.mobile2team.Data.model.Review
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener


class ReviewViewModel : ViewModel() {
    var content by mutableStateOf("")
    var rating by mutableStateOf(0)
    var errorMessage by mutableStateOf<String?>(null)

    var userReviews by mutableStateOf(listOf<Review>())
        private set
    
    var reviewCount by mutableStateOf(0)
        private set

    var averageRating by mutableStateOf(0.0f)
        private set

    private fun calculateAverageRating() {
        averageRating = if (userReviews.isNotEmpty()) {
            val average = userReviews.map { it.rating }.average()
            (kotlin.math.round(average * 10) / 10.0).toFloat()
        } else {
            0.0f
        }
    }

    fun submitReview(facilityId: String, userId: String) {
        if (content.isBlank() || rating == 0) {
            errorMessage = "내용과 별점을 입력하세요"
            return
        }

        val newReview = Review(userId = userId, content = content.trim(), rating = rating)
        userReviews = listOf(newReview) + userReviews
        reviewCount = userReviews.size
        calculateAverageRating()

        // Firebase에 리뷰 저장
        val database = FirebaseDatabase.getInstance()

        // 시설 ID 변환
        val safeFacilityId = facilityId
            .replace(".", ",")
            .replace("#", ",")
            .replace("$", ",")
            .replace("[", ",")
            .replace("]", ",")

        Log.d("ReviewViewModel", "Transformed Facility ID: $safeFacilityId")

        val reviewsRef = database.getReference("facilities/$safeFacilityId/reviews")

        // 리뷰 ID 생성 (예: UUID 사용)
        val reviewId = reviewsRef.push().key ?: return

        // 리뷰 데이터 저장
        reviewsRef.child(reviewId).setValue(newReview)
            .addOnSuccessListener {
                // 리뷰 추가 성공
            }
            .addOnFailureListener { exception ->
                // 리뷰 추가 실패
            }

        content = ""
        rating = 0
        errorMessage = null
    }

    fun fetchReviews(facilityId: String) {
        val database = FirebaseDatabase.getInstance()
        val safeFacilityId = facilityId
            .replace(".", ",")
            .replace("#", ",")
            .replace("$", ",")
            .replace("[", ",")
            .replace("]", ",")

        val reviewsRef = database.getReference("facilities/$safeFacilityId/reviews")

        reviewsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reviewsMap = snapshot.getValue(object : GenericTypeIndicator<Map<String, Review>>() {})
                userReviews = reviewsMap?.values?.toList() ?: emptyList()
                reviewCount = userReviews.size
                calculateAverageRating()
            }

            override fun onCancelled(error: DatabaseError) {
                errorMessage = "리뷰를 불러오지 못했습니다: ${error.message}"
            }
        })
    }
}