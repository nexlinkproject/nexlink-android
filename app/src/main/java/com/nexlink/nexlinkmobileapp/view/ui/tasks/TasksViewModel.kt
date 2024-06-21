package com.nexlink.nexlinkmobileapp.view.ui.tasks

import androidx.lifecycle.ViewModel
import com.nexlink.nexlinkmobileapp.data.repository.TasksRepository

class TasksViewModel(private val repository: TasksRepository) : ViewModel() {
    fun getTaskById(taskId: String) = repository.getTaskById(taskId)

    fun getTaskUser(userId: String) = repository.getTaskUser(userId)

    fun createTask(
        name: String,
        description: String,
        status: String,
        startDate: String,
        endDate: String,
        priority: String,
        projectId: String
    ) = repository.createTask(name, description, status, startDate, endDate, priority, projectId)

    fun updateTask(
        taskId: String,
        name: String,
        description: String,
        status: String,
        startDate: String,
        endDate: String,
        priority: String,
        projectId: String
    ) = repository.updateTask(taskId, name, description, status, startDate, endDate, priority, projectId)

    fun updateTaskPartial(taskId: String, fields: Map<String, Any>) = repository.updateTaskPartial(taskId, fields)

    fun deleteTask(taskId: String) = repository.deleteTask(taskId)
}