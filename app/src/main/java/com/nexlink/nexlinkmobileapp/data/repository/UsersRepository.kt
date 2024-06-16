package com.nexlink.nexlinkmobileapp.data.repository

import androidx.lifecycle.liveData
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.data.remote.retrofit.ApiService

class UsersRepository private constructor(
    private val usersApiService: ApiService,
) {
    fun getUsers() = liveData {
        emit(ResultState.Loading)

        try {
            val successResponse = usersApiService.getAllUsers()
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    companion object {
        fun getInstance(apiService: ApiService) = UsersRepository(apiService)
    }
}