package com.nexlink.nexlinkmobileapp.data.remote.request.tasks

data class CreateTasksRequest (
    val name: String,
    val description: String,
    val status: String,
    val startDate: String,
    val endDate: String,
    val priority: String,
    val projectId: String
)