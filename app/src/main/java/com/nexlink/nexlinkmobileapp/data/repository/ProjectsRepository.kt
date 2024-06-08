package com.nexlink.nexlinkmobileapp.data.repository

import android.util.Log
import androidx.lifecycle.liveData
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.data.remote.request.projects.CreateProjectRequest
import com.nexlink.nexlinkmobileapp.data.remote.retrofit.ApiService

class ProjectsRepository private constructor(
    private val projectsApiService: ApiService,
) {
    fun getProjects() = liveData {
        emit(ResultState.Loading)

        try {
            val successResponse = projectsApiService.getProjects()
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun createProject(
        name: String,
        description: String,
        status: String,
        startDate: String,
        endDate: String,
        deadline: String,
    ) = liveData {
        emit(ResultState.Loading)

        try {
            val projectRequest = CreateProjectRequest(
                name = name,
                description = description,
                status = status,
                startDate = startDate,
                endDate = endDate,
                deadline = deadline
            )

            val successResponse = projectsApiService.createProjects(projectRequest)
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    companion object {
        fun getInstance(apiService: ApiService) = ProjectsRepository(apiService)
    }
}