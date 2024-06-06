package com.nexlink.nexlinkmobileapp.data.remote.retrofit

import com.nexlink.nexlinkmobileapp.data.remote.response.projects.AllProjectsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("projects")
    suspend fun getProjects(
    ): AllProjectsResponse
}

