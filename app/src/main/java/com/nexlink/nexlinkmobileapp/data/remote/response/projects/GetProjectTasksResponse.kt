package com.nexlink.nexlinkmobileapp.data.remote.response.projects

import com.google.gson.annotations.SerializedName

data class GetProjectTasksResponse(

	@field:SerializedName("data")
	val data: DataProjectTasks? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataProjectTasks(

	@field:SerializedName("tasks")
	val tasks: List<ListProjectTasksItem?>? = null
)

data class ListProjectTasksItem(
	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("startDate")
	val startDate: String? = null,

	@field:SerializedName("endDate")
	val endDate: String? = null,

	@field:SerializedName("deadline")
	val deadline: Any? = null,

	@field:SerializedName("projectId")
	val projectId: String? = null,

	@field:SerializedName("priority")
	val priority: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
