package com.nexlink.nexlinkmobileapp.data.repository

import androidx.lifecycle.liveData
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.data.remote.retrofit.ApiService

class UsersRepository private constructor(
    private val usersApiService: ApiService,
) {
    fun getAllUsers() = liveData {
        emit(ResultState.Loading)

        try {
            val successResponse = usersApiService.getAllUsers()
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun getUserById(userId: String) = liveData {
        emit(ResultState.Loading)

        try {
            val successResponse = usersApiService.getUserById(userId)
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun addUserToProject(userId: String, projectId: String) = liveData {
        emit(ResultState.Loading)

        try {
            val successResponse = usersApiService.addUserToProject(userId, projectId)
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun addUserToTask(userId: String, taskId: String) = liveData {
        emit(ResultState.Loading)

        try {
            val successResponse = usersApiService.addUserToTask(userId, taskId)
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun removeUserFromProject(userId: String, projectId: String) = liveData {
        emit(ResultState.Loading)

        try {
            val successResponse = usersApiService.removeUserFromProject(userId, projectId)
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun removeUserFromTask(userId: String, taskId: String) = liveData {
        emit(ResultState.Loading)

        try {
            val successResponse = usersApiService.removeUserFromTask(userId, taskId)
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    companion object {
        fun getInstance(apiService: ApiService) = UsersRepository(apiService)
    }
}