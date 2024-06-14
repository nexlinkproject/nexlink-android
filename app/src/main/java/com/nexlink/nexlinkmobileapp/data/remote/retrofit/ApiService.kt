package com.nexlink.nexlinkmobileapp.data.remote.retrofit

import com.nexlink.nexlinkmobileapp.data.remote.request.auth.LoginRequest
import com.nexlink.nexlinkmobileapp.data.remote.request.auth.SignUpRequest
import com.nexlink.nexlinkmobileapp.data.remote.request.projects.CreateProjectRequest
import com.nexlink.nexlinkmobileapp.data.remote.response.auth.LoginResponse
import com.nexlink.nexlinkmobileapp.data.remote.response.auth.SignUpResponse
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.AllProjectsResponse
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.OneProjectResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("projects")
    suspend fun getProjects(
        @Query("status") status: String? = null,
    ): AllProjectsResponse

    @Headers("Content-Type: application/json")
    @POST("projects")
    suspend fun createProjects(
        @Body project: CreateProjectRequest,
    ): OneProjectResponse

    @Headers("Content-Type: application/json")
    @POST("auth/login")
    suspend fun login(
        @Body login: LoginRequest,
    ): LoginResponse

    @Headers("Content-Type: application/json")
    @POST("auth/register")
    suspend fun register(
        @Body register: SignUpRequest,
    ): SignUpResponse
}
