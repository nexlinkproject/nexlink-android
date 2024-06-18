package com.nexlink.nexlinkmobileapp.view.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nexlink.nexlinkmobileapp.data.di.Injection
import com.nexlink.nexlinkmobileapp.data.repository.TasksRepository
import com.nexlink.nexlinkmobileapp.view.ui.tasks.TasksViewModel

class TasksModelFactory(private val tasksRepository: TasksRepository) : ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TasksViewModel::class.java) -> {
                TasksViewModel(tasksRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        fun getInstance(context: Context) = TasksModelFactory(Injection.provideTasksRepository(context))
    }
}