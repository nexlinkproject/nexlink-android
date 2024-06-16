package com.nexlink.nexlinkmobileapp.data.remote.response.users

import com.google.gson.annotations.SerializedName

data class AllUsersResponse(

	@field:SerializedName("data")
	val data: DataAllUsers? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null,
)

data class ListAllUsersItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("resetPasswordToken")
	val resetPasswordToken: Any? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("resetPasswordExpires")
	val resetPasswordExpires: Any? = null,

	@field:SerializedName("fullName")
	val fullName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,
)

data class DataAllUsers(

	@field:SerializedName("users")
	val users: List<ListAllUsersItem?>? = null,
)
