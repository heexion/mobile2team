package com.example.mobile2team.Data.api

import com.example.mobile2team.Data.model.RegisterRequest
import com.example.mobile2team.Data.model.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {
    @POST("/register")
    suspend fun register(
        @Body user: RegisterRequest
    ): RegisterResponse
}
