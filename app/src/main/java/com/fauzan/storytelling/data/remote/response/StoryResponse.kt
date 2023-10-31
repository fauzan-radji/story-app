package com.fauzan.storytelling.data.remote.response

import com.google.gson.annotations.SerializedName

data class StoryResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("story")
	val story: Story
)