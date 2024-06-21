package com.nexlink.nexlinkmobileapp.data.repository

import androidx.lifecycle.liveData
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.data.remote.retrofit.ApiService

class MLRepository private constructor(
    private val mlApiService: ApiService,
) {
    fun scheduleML(projectId: String) = liveData {
        emit(ResultState.Loading)

        try {
            val successResponse = mlApiService.scheduleML(projectId)
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun feedbackML(projectId: String) = liveData {
        emit(ResultState.Loading)

        try {
            val successResponse = mlApiService.feedbackML(projectId)
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    companion object {
        fun getInstance(apiService: ApiService) = MLRepository(apiService)
    }
}