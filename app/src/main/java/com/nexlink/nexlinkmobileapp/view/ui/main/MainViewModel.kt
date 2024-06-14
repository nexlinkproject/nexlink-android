package com.nexlink.nexlinkmobileapp.view.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nexlink.nexlinkmobileapp.data.local.pref.UserModel
import com.nexlink.nexlinkmobileapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class MainViewModel(private val authRepository: AuthRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return authRepository.getSession().asLiveData()
    }
}