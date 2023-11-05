package com.fauzan.storytelling.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fauzan.storytelling.data.Result
import com.fauzan.storytelling.data.StoryRepository
import com.fauzan.storytelling.data.model.StoryModel
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: StoryRepository) : ViewModel() {

    val stories: LiveData<Result<List<StoryModel>>> = repository.stories

    fun getStories(withLocation: Boolean = false) {
        viewModelScope.launch {
            repository.getStories(withLocation)
        }
    }
}