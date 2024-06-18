package com.nexlink.nexlinkmobileapp.view.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nexlink.nexlinkmobileapp.data.local.pref.UserModel
import com.nexlink.nexlinkmobileapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    fun login(email: String, password: String) = authRepository.login(email, password)

    fun getSession(): LiveData<UserModel> {
        return authRepository.getSession().asLiveData()
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            authRepository.saveSession(user)
        }
    }

    fun register(name: String, username: String, email: String, password: String) =
        authRepository.register(name, username, email, password)

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}