package com.nexlink.nexlinkmobileapp.data.remote.response.tasks

import com.google.gson.annotations.SerializedName

data class OneTaskResponse(

	@field:SerializedName("data")
	val data: DataOneTask? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Task(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("endDate")
	val endDate: String? = null,

	@field:SerializedName("ProjectId")
	val projectId: Int? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("startDate")
	val startDate: String? = null,

	@field:SerializedName("assignedTo")
	val assignedTo: Any? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class DataOneTask(

	@field:SerializedName("task")
	val task: Task? = null
)
