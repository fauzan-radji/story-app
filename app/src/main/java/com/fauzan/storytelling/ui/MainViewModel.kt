package com.fauzan.storytelling.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fauzan.storytelling.data.StoryRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}