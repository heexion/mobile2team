package com.example.mobile2team.Data.model

data class Review(
    val id: String = java.util.UUID.randomUUID().toString(),
    val userId: String,
    val content: String,
    val rating: Int
)