package com.nexlink.nexlinkmobileapp.view.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nexlink.nexlinkmobileapp.data.di.Injection
import com.nexlink.nexlinkmobileapp.data.repository.ProjectsRepository
import com.nexlink.nexlinkmobileapp.view.ui.projects.ProjectsViewModel

class ProjectsModelFactory(private val projectsRepository: ProjectsRepository) : ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProjectsViewModel::class.java) -> {
                ProjectsViewModel(projectsRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        fun getInstance(context: Context) = ProjectsModelFactory(Injection.provideProjectsRepository(context))
    }
}