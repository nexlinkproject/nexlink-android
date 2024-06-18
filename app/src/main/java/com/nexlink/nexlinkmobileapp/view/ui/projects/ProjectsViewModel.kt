package com.nexlink.nexlinkmobileapp.view.ui.projects

import androidx.lifecycle.ViewModel
import com.nexlink.nexlinkmobileapp.data.repository.ProjectsRepository

class ProjectsViewModel(private val repository: ProjectsRepository) : ViewModel() {
    fun getProjects(status: String? = null) = repository.getProjects(status)

    fun getProjectById(projectId: String) = repository.getProjectById(projectId)

    fun getProjecUsers(projectId: String) = repository.getProjecUsers(projectId)

    fun getProjectTasks(projectId: String) = repository.getProjectTasks(projectId)

    fun createProject(
        name: String,
        description: String,
        status: String,
        startDate: String,
        endDate: String,
        deadline: String,
    ) = repository.createProject(name, description, status, startDate, endDate, deadline)

    fun updateProject(
        name: String,
        description: String,
        status: String,
        startDate: String,
        endDate: String,
        deadline: String,
        projectId: String,
    ) = repository.updateProject(name, description, status, startDate, endDate, deadline, projectId)

    fun deleteProject(projectId: String) = repository.deleteProject(projectId)
}