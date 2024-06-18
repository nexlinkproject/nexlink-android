package com.nexlink.nexlinkmobileapp.view.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nexlink.nexlinkmobileapp.data.di.Injection
import com.nexlink.nexlinkmobileapp.data.repository.AuthRepository
import com.nexlink.nexlinkmobileapp.view.ui.auth.AuthViewModel

class AuthModelFactory(private val authRepository: AuthRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(authRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        fun getInstance(context: Context) =
            AuthModelFactory(Injection.provideAuthRepository(context))
    }
}