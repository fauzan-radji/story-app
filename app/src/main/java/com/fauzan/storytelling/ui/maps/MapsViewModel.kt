package com.fauzan.storytelling.ui.maps

import androidx.lifecycle.ViewModel
import com.fauzan.storytelling.data.StoryRepository

class MapsViewModel(private val repository: StoryRepository) : ViewModel() {

    fun getStories() = repository.getStoriesWithLocation()
}