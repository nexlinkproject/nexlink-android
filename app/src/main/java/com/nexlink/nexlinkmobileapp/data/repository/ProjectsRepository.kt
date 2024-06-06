package com.nexlink.nexlinkmobileapp.data.repository

import androidx.lifecycle.liveData
import com.nexlink.nexlinkmobileapp.data.ResultState
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

    companion object {
        fun getInstance(apiService: ApiService) = ProjectsRepository(apiService)
    }
}