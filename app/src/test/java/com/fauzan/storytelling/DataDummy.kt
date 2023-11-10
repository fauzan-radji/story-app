package com.fauzan.storytelling

import com.fauzan.storytelling.data.remote.response.StoriesResponse
import com.fauzan.storytelling.data.remote.response.Story

object DataDummy {
    fun generateDummyStoriesResponse(): StoriesResponse {
        val listStory = mutableListOf<Story>()
        for (i in 0..100) {
            listStory.add(
                Story(
                    photoUrl = "https://source.unsplash.com/random/${800 + i}x${600 + i}",
                    createdAt = "2021-09-01T00:00:00.000Z",
                    name = "Story $i",
                    description = "Description $i",
                    lon = 0.0,
                    id = "$i",
                    lat = 0.0
                )
            )
        }

        return StoriesResponse(listStory, false, "success")
    }
}