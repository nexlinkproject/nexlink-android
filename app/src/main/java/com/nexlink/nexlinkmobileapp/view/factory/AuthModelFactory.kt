package com.nexlink.nexlinkmobileapp.view.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nexlink.nexlinkmobileapp.data.di.Injection
import com.nexlink.nexlinkmobileapp.data.repository.AuthRepository
import com.nexlink.nexlinkmobileapp.view.ui.auth.login.LoginViewModel
import com.nexlink.nexlinkmobileapp.view.ui.auth.signup.SignUpViewModel
import com.nexlink.nexlinkmobileapp.view.ui.main.MainViewModel
import com.nexlink.nexlinkmobileapp.view.ui.profile.ProfileViewModel

class AuthModelFactory(private val authRepository: AuthRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(authRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        fun getInstance(context: Context) =
            AuthModelFactory(Injection.provideAuthRepository(context))
    }
}