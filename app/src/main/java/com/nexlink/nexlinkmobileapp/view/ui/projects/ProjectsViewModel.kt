package com.nexlink.nexlinkmobileapp.view.ui.projects

import androidx.lifecycle.ViewModel
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.ListAllProjectsItem
import com.nexlink.nexlinkmobileapp.data.repository.ProjectsRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProjectsViewModel(private val repository: ProjectsRepository) : ViewModel() {
    fun getProjects() = repository.getProjects()

    fun filterProjectsByDate(
        projects: List<ListAllProjectsItem?>,
        startDate: Date,
        endDate: Date,
    ): List<ListAllProjectsItem?> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        return projects.filter {
            val projectStartDate = dateFormat.parse(it?.startDate)
            val projectEndDate = dateFormat.parse(it?.endDate)
            projectStartDate != null && projectEndDate != null &&
                    !projectStartDate.before(startDate) && !projectEndDate.after(endDate)
        }
    }

    fun createProject(
        name: String,
        description: String,
        status: String,
        startDate: String,
        endDate: String,
        deadline: String,
    ) = repository.createProject(name, description, status, startDate, endDate, deadline)
}