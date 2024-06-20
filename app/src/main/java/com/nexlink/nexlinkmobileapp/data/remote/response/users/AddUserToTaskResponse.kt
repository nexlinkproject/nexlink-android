package com.nexlink.nexlinkmobileapp.data.remote.response.users

import com.google.gson.annotations.SerializedName

data class AddUserToTaskResponse(

	@field:SerializedName("data")
	val data: DataAddUserToTask? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class AddTaskUserItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("taskId")
	val taskId: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class DataAddUserToTask(

	@field:SerializedName("addTaskUser")
	val addTaskUser: AddTaskUserItem? = null
)
