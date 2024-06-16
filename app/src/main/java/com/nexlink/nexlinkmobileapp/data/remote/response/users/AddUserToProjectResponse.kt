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

data class DataAddUserToProject(
	val any: Any? = null
)
