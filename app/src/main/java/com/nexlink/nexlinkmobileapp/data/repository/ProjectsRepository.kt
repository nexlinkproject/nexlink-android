package com.nexlink.nexlinkmobileapp.data.repository

import androidx.lifecycle.liveData
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.data.remote.request.projects.CreateProjectRequest
import com.nexlink.nexlinkmobileapp.data.remote.retrofit.ApiService

class ProjectsRepository private constructor(
    private val projectsApiService: ApiService,
) {
    fun getProjects(status: String? = null) = liveData {
        emit(ResultState.Loading)

        try {
            val successResponse = projectsApiService.getProjects(status)
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun getProjectById(projectId: String) = liveData {
        emit(ResultState.Loading)

        try {
            val successResponse = projectsApiService.getProjectById(projectId)
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun getProjecUsers(projectId: String) = liveData {
        emit(ResultState.Loading)

        try {
            val successResponse = projectsApiService.getProjectUsers(projectId)
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun getProjectTasks(projectId: String) = liveData {
        emit(ResultState.Loading)

        try {
            val successResponse = projectsApiService.getProjectTasks(projectId)
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

    fun updateProject(
        name: String,
        description: String,
        status: String,
        startDate: String,
        endDate: String,
        deadline: String,
        projectId: String,
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

            val successResponse = projectsApiService.updateProject(projectId, projectRequest)
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun deleteProject(projectId: String) = liveData {
        emit(ResultState.Loading)

        try {
            val successResponse = projectsApiService.deleteProject(projectId)
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    companion object {
        fun getInstance(apiService: ApiService) = ProjectsRepository(apiService)
    }
}