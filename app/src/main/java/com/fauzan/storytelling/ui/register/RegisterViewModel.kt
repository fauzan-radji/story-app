package com.fauzan.storytelling.ui.register

import androidx.lifecycle.ViewModel
import com.fauzan.storytelling.data.StoryRepository

class RegisterViewModel(private val repository: StoryRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) = repository.register(name, email, password)
}