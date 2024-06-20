package com.nexlink.nexlinkmobileapp.data.remote.response.tasks

import com.google.gson.annotations.SerializedName

data class GetTaskUsersResponse(

	@field:SerializedName("data")
	val data: DataTaskUsers? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class ListTaskUsersItem(

	@field:SerializedName("TasksUsers")
	val tasksUsers: TasksUsers? = null,

	@field:SerializedName("profilePicture")
	val profilePicture: Any? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("resetPasswordToken")
	val resetPasswordToken: Any? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("resetPasswordExpires")
	val resetPasswordExpires: Any? = null,

	@field:SerializedName("signedByGoogle")
	val signedByGoogle: Any? = null,

	@field:SerializedName("fullName")
	val fullName: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class TasksUsers(

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

data class TaskUsers(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("endDate")
	val endDate: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("Users")
	val users: List<ListTaskUsersItem?>? = null,

	@field:SerializedName("deadline")
	val deadline: Any? = null,

	@field:SerializedName("priority")
	val priority: String? = null,

	@field:SerializedName("projectId")
	val projectId: String? = null,

	@field:SerializedName("startDate")
	val startDate: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class DataTaskUsers(

	@field:SerializedName("task")
	val task: TaskUsers? = null
)
