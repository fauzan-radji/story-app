package com.fauzan.storytelling.data.remote.response

import com.fauzan.storytelling.data.model.StoryModel
import com.google.gson.annotations.SerializedName

data class Story(

	@field:SerializedName("photoUrl")
	val photoUrl: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("lon")
	val lon: Any?,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("lat")
	val lat: Any?
) {
	fun toStoryModel(): StoryModel {
		return StoryModel(
			photoUrl = photoUrl,
			createdAt = createdAt,
			name = name,
			description = description,
			lon = lon,
			id = id,
			lat = lat
		)
	}
}