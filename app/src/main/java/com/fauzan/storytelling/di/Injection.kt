package com.fauzan.storytelling.di

import android.content.Context
import com.fauzan.storytelling.data.StoryRepository
import com.fauzan.storytelling.data.datastore.UserPreference
import com.fauzan.storytelling.data.datastore.dataStore
import com.fauzan.storytelling.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val preference = UserPreference.getInstance(context.dataStore)
        return StoryRepository.getInstance(apiService, preference, context)
    }
}