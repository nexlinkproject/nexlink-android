package com.nexlink.nexlinkmobileapp.data.remote.response.auth

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("data")
	val data: DataLogin? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataLogin(

	@field:SerializedName("fullName")
	val fullName: String? = null,

	@field:SerializedName("accessToken")
	val accessToken: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("refreshToken")
	val refreshToken: String? = null
)
