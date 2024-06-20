package com.nexlink.nexlinkmobileapp.view.ui.users

import androidx.lifecycle.ViewModel
import com.nexlink.nexlinkmobileapp.data.repository.UsersRepository

class UsersViewModel(private val repository: UsersRepository) : ViewModel() {
    fun getAllUsers() = repository.getAllUsers()

    fun getUserById(userId: String) = repository.getUserById(userId)

    fun addUserToProject(userId: String, projectId: String) = repository.addUserToProject(userId, projectId)

    fun addUserToTask(userId: String, taskId: String) = repository.addUserToTask(userId, taskId)

    fun removeUserFromProject(userId: String, projectId: String) = repository.removeUserFromProject(userId, projectId)

    fun removeUserFromTask(userId: String, taskId: String) = repository.removeUserFromTask(userId, taskId)
}