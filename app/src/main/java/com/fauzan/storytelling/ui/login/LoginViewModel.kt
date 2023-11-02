package com.fauzan.storytelling.ui.login

import androidx.lifecycle.ViewModel
import com.fauzan.storytelling.data.StoryRepository

class LoginViewModel(private val repository: StoryRepository) : ViewModel() {
    fun login(email: String, password: String) = repository.login(email, password)
}