package com.nexlink.nexlinkmobileapp.view.ui.projects

import androidx.lifecycle.ViewModel
import com.nexlink.nexlinkmobileapp.data.repository.ProjectsRepository

class ProjectsViewModel(private val repository: ProjectsRepository) : ViewModel() {
    fun getProjects() = repository.getProjects()
}