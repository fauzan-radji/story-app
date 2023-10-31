package com.fauzan.storytelling.data.remote.response

import com.fauzan.storytelling.data.model.StoryModel
import com.google.gson.annotations.SerializedName

data class StoriesResponse(

	@field:SerializedName("listStory")
	val listStory: List<Story>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
) {
	fun toStoryModel(): List<StoryModel> {
		val listStoryModel = mutableListOf<StoryModel>()
		listStory.forEach {
			listStoryModel.add(it.toStoryModel())
		}
		return listStoryModel
	}
}