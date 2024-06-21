package com.nexlink.nexlinkmobileapp.data.remote.response.ml

import com.google.gson.annotations.SerializedName

data class ScheduleResponse(

	@field:SerializedName("data")
	val data: List<ListDataScheduleItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class ListDataScheduleItem(

	@field:SerializedName("dueDate")
	val dueDate: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("projectId")
	val projectId: String? = null,

	@field:SerializedName("userID")
	val userID: List<String?>? = null,

	@field:SerializedName("startDate")
	val startDate: String? = null,

	@field:SerializedName("taskId")
	val taskId: String? = null
)
