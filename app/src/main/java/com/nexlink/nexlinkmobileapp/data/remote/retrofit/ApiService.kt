package com.nexlink.nexlinkmobileapp.data.remote.retrofit

import com.nexlink.nexlinkmobileapp.data.remote.request.projects.CreateProjectRequest
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.AllProjectsResponse
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.OneProjectResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @GET("projects")
    suspend fun getProjects(
    ): AllProjectsResponse

    @Headers("Content-Type: application/json")
    @POST("projects")
    suspend fun createProjects(
        @Body project: CreateProjectRequest
    ): OneProjectResponse
}
