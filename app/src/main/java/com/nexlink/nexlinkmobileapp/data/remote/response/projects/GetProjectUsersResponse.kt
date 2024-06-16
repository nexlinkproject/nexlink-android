package com.nexlink.nexlinkmobileapp.data.remote.response.projects

import com.google.gson.annotations.SerializedName

data class GetProjectUsersResponse(

	@field:SerializedName("data")
	val data: DataProjectUsers? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class ListProjectUsersItem(

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

	@field:SerializedName("ProjectsUsers")
	val projectsUsers: ProjectsUsers? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class DataProjectUsers(

	@field:SerializedName("project")
	val project: ProjectUsers? = null
)

data class ProjectsUsers(

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

data class ProjectUsers(

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
	val users: List<ListProjectUsersItem?>? = null,

	@field:SerializedName("deadline")
	val deadline: String? = null,

	@field:SerializedName("startDate")
	val startDate: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
