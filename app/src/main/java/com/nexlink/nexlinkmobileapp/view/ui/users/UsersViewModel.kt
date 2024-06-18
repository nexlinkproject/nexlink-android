package com.nexlink.nexlinkmobileapp.view.ui.users

import androidx.lifecycle.ViewModel
import com.nexlink.nexlinkmobileapp.data.repository.UsersRepository

class UsersViewModel(private val repository: UsersRepository) : ViewModel() {
    fun getUserById(userId: String) = repository.getUserById(userId)
}