package com.nexlink.nexlinkmobileapp.view.ui.tasks

import androidx.lifecycle.ViewModel
import com.nexlink.nexlinkmobileapp.data.repository.TasksRepository

class TasksViewModel(private val repository: TasksRepository) : ViewModel() {
    fun getTaskById(taskId: String) = repository.getTaskById(taskId)

    fun createTask(
        name: String,
        description: String,
        status: String,
        startDate: String,
        endDate: String,
        priority: String,
        projectId: String
    ) = repository.createTask(name, description, status, startDate, endDate, priority, projectId)

//    fun deleteTask(taskId: String) = repository.deleteTask(taskId)
}