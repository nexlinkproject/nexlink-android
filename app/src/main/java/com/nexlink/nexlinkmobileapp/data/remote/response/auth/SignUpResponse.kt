package com.nexlink.nexlinkmobileapp.data.remote.response.auth

import com.google.gson.annotations.SerializedName

data class SignUpResponse(

	@field:SerializedName("data")
	val data: DataSignUp? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataSignUp(

	@field:SerializedName("user")
	val user: UserSignUp? = null
)

data class UserSignUp(

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

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
