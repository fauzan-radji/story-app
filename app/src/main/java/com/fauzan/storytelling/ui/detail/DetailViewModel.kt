package com.fauzan.storytelling.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fauzan.storytelling.data.StoryRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: StoryRepository) : ViewModel() {
    val story = repository.story

    // create livedata from here not repository
    private val _latLng: MutableLiveData<LatLng?> = MutableLiveData(null)
    val latLng: LiveData<LatLng?> = _latLng

    fun setLatLng(lat: Double?, lon: Double?) {
        if (lat != null && lon != null) {
            _latLng.value = LatLng(lat, lon)
        } else {
            _latLng.value = null
        }
    }

    fun getStory(storyId: String) {
        viewModelScope.launch {
            repository.getStory(storyId)
        }
    }
}