package com.nexlink.nexlinkmobileapp.view.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nexlink.nexlinkmobileapp.data.di.Injection
import com.nexlink.nexlinkmobileapp.data.repository.UsersRepository
import com.nexlink.nexlinkmobileapp.view.ui.users.UsersViewModel

class UsersModelFactory (private val usersRepository: UsersRepository) : ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UsersViewModel::class.java) -> {
                UsersViewModel(usersRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        fun getInstance(context: Context) = UsersModelFactory(Injection.provideUsersRepository(context))
    }
}