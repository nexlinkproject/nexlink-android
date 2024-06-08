package com.nexlink.nexlinkmobileapp.data.remote.request.projects

data class CreateProjectRequest(
    val name: String,
    val description: String,
    val status: String,
    val startDate: String,
    val endDate: String,
    val deadline: String
)
