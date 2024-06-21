package com.nexlink.nexlinkmobileapp.data.repository

import androidx.lifecycle.liveData
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.data.remote.request.tasks.CreateTasksRequest
import com.nexlink.nexlinkmobileapp.data.remote.retrofit.ApiService

class TasksRepository private constructor(
    private val tasksApiService: ApiService,
) {
    fun getTasks() = liveData {
        emit(ResultState.Loading)

        try {
            val successResponse = tasksApiService.getAllTasks()
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun getTasksByUserId(userId: String, status: String? = null, date: String? = null) = liveData {
        emit(ResultState.Loading)

        try {
            val successResponse = tasksApiService.getTasksByUserId(userId, status, date)
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun getTaskUser(userId: String) = liveData {
        emit(ResultState.Loading)

        try {
            val successResponse = tasksApiService.getTaskUsers(userId)
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun getTaskById(taskId: String) = liveData {
        emit(ResultState.Loading)

        try {
            val successResponse = tasksApiService.getTaskById(taskId)
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun createTask(
        name: String,
        description: String,
        status: String,
        startDate: String,
        endDate: String,
        priority: String,
        projectId: String,
    ) = liveData {
        emit(ResultState.Loading)

        try {
            val taskRequest = CreateTasksRequest(
                name = name,
                description = description,
                status = status,
                startDate = startDate,
                endDate = endDate,
                priority = priority,
                projectId = projectId
            )
            val successResponse = tasksApiService.createTasks(taskRequest)
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun updateTask(
        taskId: String,
        name: String,
        description: String,
        status: String,
        startDate: String,
        endDate: String,
        priority: String,
        projectId: String,
    ) = liveData {
        emit(ResultState.Loading)

        try {
            val taskRequest = CreateTasksRequest(
                name = name,
                description = description,
                status = status,
                startDate = startDate,
                endDate = endDate,
                priority = priority,
                projectId = projectId
            )
            val successResponse = tasksApiService.updateTask(taskId, taskRequest)
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun updateTaskPartial(taskId: String, fields: Map<String, Any>) = liveData {
        emit(ResultState.Loading)

        try {
            val successResponse = tasksApiService.updateTaskPartial(taskId, fields)
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun deleteTask(taskId: String) = liveData {
        emit(ResultState.Loading)

        try {
            val successResponse = tasksApiService.deleteTask(taskId)
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    companion object {
        fun getInstance(apiService: ApiService) = TasksRepository(apiService)
    }
}