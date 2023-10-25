package com.fauzan.storytelling.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fauzan.storytelling.data.Result
import com.fauzan.storytelling.data.StoryRepository
import com.fauzan.storytelling.data.model.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: StoryRepository) : ViewModel() {

    val result: LiveData<Result<UserModel?>> = repository.userModel

    fun login(email: String, password: String) {
        viewModelScope.launch {
            repository.login(email, password)
        }
    }

    fun checkSession() {
        viewModelScope.launch {
            repository.checkSession()
        }
    }
}