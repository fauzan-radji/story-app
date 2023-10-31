package com.fauzan.storytelling.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fauzan.storytelling.data.StoryRepository
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: StoryRepository) : ViewModel() {
    val story = repository.story

    fun getStory(storyId: String) {
        viewModelScope.launch {
            repository.getStory(storyId)
        }
    }
}