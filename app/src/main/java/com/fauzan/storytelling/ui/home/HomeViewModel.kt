package com.fauzan.storytelling.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fauzan.storytelling.data.StoryRepository
import com.fauzan.storytelling.data.model.StoryModel

class HomeViewModel(private val repository: StoryRepository) : ViewModel() {


    fun getStories(): LiveData<PagingData<StoryModel>> = repository.getStories().cachedIn(viewModelScope)

    fun checkSession() = repository.checkSession()
}