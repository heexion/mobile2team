package com.example.mobile2team.Data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://your-backend.com") //백엔드 API의 기본 주소
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
