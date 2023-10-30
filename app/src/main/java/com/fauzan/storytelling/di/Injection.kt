package com.fauzan.storytelling.di

import android.content.Context
import com.fauzan.storytelling.data.StoryRepository
import com.fauzan.storytelling.data.datastore.UserPreference
import com.fauzan.storytelling.data.datastore.dataStore
import com.fauzan.storytelling.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val preference = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { preference.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token ?: "")
        return StoryRepository.getInstance(apiService, preference, context)
    }
}