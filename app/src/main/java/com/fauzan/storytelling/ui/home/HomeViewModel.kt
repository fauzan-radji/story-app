package com.fauzan.storytelling.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fauzan.storytelling.data.Result
import com.fauzan.storytelling.data.StoryRepository
import com.fauzan.storytelling.data.model.UserModel
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: StoryRepository) : ViewModel() {

    val userModel: LiveData<Result<UserModel?>> = repository.userModel

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}