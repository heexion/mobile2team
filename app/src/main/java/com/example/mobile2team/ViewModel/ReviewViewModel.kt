package com.example.mobile2team.ViewModel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.mobile2team.Data.model.Review



class ReviewViewModel : ViewModel() {
    var content by mutableStateOf("")
    var rating by mutableStateOf(0)
    var errorMessage by mutableStateOf<String?>(null)

    var userReviews by mutableStateOf(listOf<Review>())
        private set

    fun submitReview(userId: String = "me") {
        if (content.isBlank() || rating == 0) {
            errorMessage = "내용과 별점을 입력하세요"
            return
        }

        val newReview = Review(userId = userId, content = content.trim(), rating = rating)
        userReviews = listOf(newReview) + userReviews

        content = ""
        rating = 0
        errorMessage = null
    }
}