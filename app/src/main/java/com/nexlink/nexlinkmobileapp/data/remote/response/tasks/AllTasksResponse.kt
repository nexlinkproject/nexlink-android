package com.nexlink.nexlinkmobileapp.data.remote.response.tasks

import com.google.gson.annotations.SerializedName

data class AllTasksResponse(

    @field:SerializedName("data")
	val data: DataAllTasks? = null,

    @field:SerializedName("message")
	val message: String? = null,

    @field:SerializedName("status")
	val status: String? = null
)

data class DataAllTasks(

	@field:SerializedName("tasks")
	val tasks: List<ListAllTasksItem?>? = null
)

data class ListAllTasksItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("endDate")
	val endDate: String? = null,

	@field:SerializedName("ProjectId")
	val projectId: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("startDate")
	val startDate: String? = null,

	@field:SerializedName("assignedTo")
	val assignedTo: Any? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("priority")
	val priority: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
