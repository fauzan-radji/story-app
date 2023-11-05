package com.fauzan.storytelling.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fauzan.storytelling.data.Result
import com.fauzan.storytelling.data.StoryRepository
import com.fauzan.storytelling.data.model.StoryModel
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: StoryRepository) : ViewModel() {

    val posts: LiveData<Result<List<StoryModel>>> = repository.stories

    fun getStories() {
        viewModelScope.launch {
            repository.getStories()
        }
    }

    fun checkSession() = repository.checkSession()
}