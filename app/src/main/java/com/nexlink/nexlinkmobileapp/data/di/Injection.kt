package com.nexlink.nexlinkmobileapp.data.di

import android.content.Context
import com.nexlink.nexlinkmobileapp.data.remote.retrofit.ApiConfig
import com.nexlink.nexlinkmobileapp.data.repository.ProjectsRepository

object Injection {
    fun provideProjectsRepository(context: Context): ProjectsRepository {
//        val pref = UserPreference.getInstance(context.dataStore)
//        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService()
        return ProjectsRepository.getInstance(apiService)
    }
}