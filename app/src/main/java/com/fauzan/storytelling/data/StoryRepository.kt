package com.fauzan.storytelling.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.fauzan.storytelling.R
import com.fauzan.storytelling.data.datastore.UserPreference
import com.fauzan.storytelling.data.local.StoryDatabase
import com.fauzan.storytelling.data.model.StoryModel
import com.fauzan.storytelling.data.model.UserModel
import com.fauzan.storytelling.data.paging.StoryRemoteMediator
import com.fauzan.storytelling.data.remote.response.ErrorResponse
import com.fauzan.storytelling.data.remote.retrofit.ApiService
import com.fauzan.storytelling.utils.reduceFileImage
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository private constructor(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val context: Context
) {
//    private val _stories = MediatorLiveData<Result<List<StoryModel>>>()
//    val stories: LiveData<Result<List<StoryModel>>> = _stories

    private val _story = MediatorLiveData<Result<StoryModel>>()
    val story: LiveData<Result<StoryModel>> = _story

    fun login(email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            if (response.error) {
                emit(Result.Error(response.message))
            } else {
                val name = response.loginResult.name
                val token = response.loginResult.token
                val userModel = UserModel(name, email, token)
                userPreference.saveSession(userModel)
                emit(Result.Success(true))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            emit(Result.Error(errorBody.message ?: e.message.toString()))
        }
    }

    fun register(name: String, email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            if (response.error == true) {
                emit(Result.Error(response.message ?: context.getString(R.string.something_went_wrong)))
            } else {
                emit(Result.Success(response.message ?: context.getString(R.string.success)))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
           emit(Result.Error(errorBody.message ?: e.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun checkSession() = userPreference.getSession().asLiveData()
    private suspend fun getToken(): String = userPreference.getToken()

    suspend fun logout() {
        userPreference.clearSession()
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getStories(): LiveData<PagingData<StoryModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
            ),
            remoteMediator = StoryRemoteMediator(database, apiService, userPreference),
            pagingSourceFactory = { database.storyDao().getStories() }
        ).liveData
    }

    fun getStoriesWithLocation() = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStories(getToken(), location = 1)
            if (response.error) {
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(response.toStoryModel()))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            emit(Result.Error(errorBody.message ?: e.message.toString()))
        }
    }

    suspend fun getStory(id: String) {
        _story.value = Result.Loading
        try {
            val response = apiService.getStory(getToken(), id)
            if (response.error) {
                _story.value = Result.Error(response.message)
            } else {
                _story.value = Result.Success(response.story.toStoryModel())
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            _story.value = Result.Error(errorBody.message ?: e.message.toString())
        }
    }

    fun addStory(description: String, imageFile: File) = liveData {
        emit(Result.Loading)

        val compressedFile = imageFile.reduceFileImage()
        val descriptionRequest = description.toRequestBody("text/plain".toMediaType())
        val photoRequest = compressedFile.asRequestBody("image/jpeg".toMediaType())
        val photoMultipartBody = MultipartBody.Part.createFormData(
            "photo",
            compressedFile.name,
            photoRequest
        )

        try {
            val response = apiService.addStory(getToken(), descriptionRequest, photoMultipartBody)
            if (response.error == true) {
                emit(Result.Error(response.message ?: context.getString(R.string.something_went_wrong)))
            } else {
                emit(Result.Success(response.message ?: context.getString(R.string.success)))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            emit(Result.Error(errorBody.message ?: e.message.toString()))
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(database: StoryDatabase, apiService: ApiService, userPreference: UserPreference, context: Context): StoryRepository {
            return instance ?: synchronized(this) {
                instance ?: StoryRepository(database, apiService, userPreference, context).also { instance = it }
            }
        }
    }
}