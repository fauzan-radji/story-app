package com.fauzan.storytelling.ui.add

import androidx.lifecycle.ViewModel
import com.fauzan.storytelling.data.StoryRepository
import java.io.File

class AddViewModel(private val repository: StoryRepository) : ViewModel() {
    fun addStory(description: String, imageFile: File) = repository.addStory(description, imageFile)
}