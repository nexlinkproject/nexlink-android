package com.nexlink.nexlinkmobileapp.data.repository

import androidx.lifecycle.liveData
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.data.local.pref.UserModel
import com.nexlink.nexlinkmobileapp.data.local.pref.UserPreference
import com.nexlink.nexlinkmobileapp.data.remote.request.auth.LoginRequest
import com.nexlink.nexlinkmobileapp.data.remote.request.auth.SignUpRequest
import com.nexlink.nexlinkmobileapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow

class AuthRepository private constructor(
    private val authApiService: ApiService,
    private val userPreference: UserPreference,
) {
    fun register(name: String, username: String, email: String, password: String) = liveData {
        emit(ResultState.Loading)

        try {
            val signUpRequest = SignUpRequest(
                fullName = name,
                username = username,
                email = email,
                password = password
            )

            val successResponse = authApiService.register(signUpRequest)
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun login(email: String, password: String) = liveData {
        emit(ResultState.Loading)

        try {
            val loginRequest = LoginRequest(
                email = email,
                password = password
            )

            val successResponse = authApiService.login(loginRequest)
            emit(ResultState.Success(successResponse))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        fun getInstance(apiService: ApiService, userPreference: UserPreference) =
            AuthRepository(apiService, userPreference)
    }
}