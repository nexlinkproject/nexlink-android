package com.nexlink.nexlinkmobileapp.data.repository

import androidx.lifecycle.liveData
import com.nexlink.nexlinkmobileapp.data.ResultState
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

    companion object {
        fun getInstance(apiService: ApiService) = TasksRepository(apiService)
    }
}