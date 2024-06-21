package com.nexlink.nexlinkmobileapp.data.remote.response.ml

import com.google.gson.annotations.SerializedName

data class FeedbackResponse(

	@field:SerializedName("data")
	val data: List<ListDataFeedbackItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class ListDataFeedbackItem(

	@field:SerializedName("sentences")
	val sentences: String? = null,

	@field:SerializedName("label_task")
	val labelTask: String? = null
)
