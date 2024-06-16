package com.nexlink.nexlinkmobileapp.data.remote.response.projects

import com.google.gson.annotations.SerializedName

data class DeleteProjectResponse(

	@field:SerializedName("data")
	val data: DataDeleteProject? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataDeleteProject(
	val any: Any? = null
)
