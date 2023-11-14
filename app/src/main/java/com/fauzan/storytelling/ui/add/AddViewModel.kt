package com.fauzan.storytelling.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fauzan.storytelling.data.StoryRepository
import kotlinx.coroutines.launch
import java.io.File

class AddViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _lat: MutableLiveData<Double?> = MutableLiveData(null)
    val lat: LiveData<Double?> = _lat

    private val _lon: MutableLiveData<Double?> = MutableLiveData(null)
    val lon: LiveData<Double?> = _lon

    val isIncludeLocation: LiveData<Boolean> = repository.getIncludeLocation()

    fun setMyLocation(latitude: Double?, longitude: Double?) {
        _lat.value = latitude
        _lon.value = longitude

        viewModelScope.launch {
            repository.setIncludeLocation(latitude != null && longitude != null)
        }
    }
    fun addStory(description: String, imageFile: File) = repository.addStory(description, imageFile, lat.value, lon.value)
}