package com.nexlink.nexlinkmobileapp.view.ui.auth.signup

import androidx.lifecycle.ViewModel
import com.nexlink.nexlinkmobileapp.data.repository.AuthRepository

class SignUpViewModel(private val repository: AuthRepository) : ViewModel() {
    fun register(name: String, username: String, email: String, password: String) =
        repository.register(name, username, email, password)
}