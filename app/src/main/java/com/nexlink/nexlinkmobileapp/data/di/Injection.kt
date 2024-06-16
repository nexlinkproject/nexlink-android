package com.nexlink.nexlinkmobileapp.data.di

import android.content.Context
import com.nexlink.nexlinkmobileapp.data.local.pref.UserPreference
import com.nexlink.nexlinkmobileapp.data.local.pref.dataStore
import com.nexlink.nexlinkmobileapp.data.remote.retrofit.ApiConfig
import com.nexlink.nexlinkmobileapp.data.repository.AuthRepository
import com.nexlink.nexlinkmobileapp.data.repository.ProjectsRepository
import com.nexlink.nexlinkmobileapp.data.repository.TasksRepository
import com.nexlink.nexlinkmobileapp.data.repository.UsersRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { userPreference.getSession().first() }
        val apiService = ApiConfig.getApiService(user.accessToken)
        return AuthRepository.getInstance(apiService, userPreference)
    }

    fun provideProjectsRepository(context: Context): ProjectsRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.accessToken)
        return ProjectsRepository.getInstance(apiService)
    }

    fun provideUsersRepository(context: Context): UsersRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { userPreference.getSession().first() }
        val apiService = ApiConfig.getApiService(user.accessToken)
        return UsersRepository.getInstance(apiService)
    }

    fun provideTasksRepository(context: Context): TasksRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { userPreference.getSession().first() }
        val apiService = ApiConfig.getApiService(user.accessToken)
        return TasksRepository.getInstance(apiService)
    }
}