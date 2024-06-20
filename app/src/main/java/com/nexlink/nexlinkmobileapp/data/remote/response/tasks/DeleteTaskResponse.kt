package com.nexlink.nexlinkmobileapp.data.remote.response.tasks

import com.google.gson.annotations.SerializedName

data class DeleteTaskResponse(

	@field:SerializedName("data")
	val data: DataDeleteTask? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataDeleteTask(
	val any: Any? = null
)
