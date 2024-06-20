package com.nexlink.nexlinkmobileapp.data.remote.response.users

import com.google.gson.annotations.SerializedName

data class AddUserToProjectResponse(

	@field:SerializedName("data")
	val data: DataAddUserToProject? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class AddProjectUserItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("projectId")
	val projectId: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class DataAddUserToProject(

	@field:SerializedName("addProjectUser")
	val addProjectUser: AddProjectUserItem? = null
)
