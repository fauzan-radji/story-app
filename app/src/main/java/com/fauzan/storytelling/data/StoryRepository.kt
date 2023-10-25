package com.fauzan.storytelling.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asLiveData
import com.fauzan.storytelling.R
import com.fauzan.storytelling.data.datastore.UserPreference
import com.fauzan.storytelling.data.model.UserModel
import com.fauzan.storytelling.data.remote.response.ErrorResponse
import com.fauzan.storytelling.data.remote.retrofit.ApiService
import com.google.gson.Gson
import retrofit2.HttpException

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val context: Context
) {
    private val _userModel = MediatorLiveData<Result<UserModel?>>()
    val userModel: LiveData<Result<UserModel?>> = _userModel

    private val _registerResult = MediatorLiveData<Result<Boolean>>()
    val registerResult: LiveData<Result<Boolean>> = _registerResult

    suspend fun login(email: String, password: String) {
        _userModel.value = Result.Loading
        try {
            val response = apiService.login(email, password)
            if (response.error) {
                _userModel.value = Result.Error(response.message)
            } else {
                val name = response.loginResult.name
                val token = response.loginResult.token
                val userModel = UserModel(name, email, token)
                userPreference.saveSession(userModel)
                _userModel.value = Result.Success(userModel)
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            _userModel.value = Result.Error(errorBody.message ?: e.message.toString())
        }
    }

    suspend fun register(name: String, email: String, password: String) {
        _registerResult.value = Result.Loading
        try {
            val response = apiService.register(name, email, password)
            if (response.error == true) {
                _registerResult.value = Result.Error(response.message ?: context.getString(R.string.something_went_wrong))
            } else {
                _registerResult.value = Result.Success(true)
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            _registerResult.value = Result.Error(errorBody.message ?: e.message.toString())
        }
    }

    fun checkSession() {
        _userModel.value = Result.Loading
        _userModel.addSource(userPreference.getSession().asLiveData()) { userModel ->
            if (userModel.token.isNullOrEmpty()) {
                _userModel.value = Result.Error(context.getString(R.string.session_expired_please_login_again))
            } else {
                _userModel.value = Result.Success(userModel)
            }
        }
    }

    suspend fun logout() {
        userPreference.clearSession()
        _userModel.value = Result.Success(null)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference, context: Context): StoryRepository {
            return instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference, context).also { instance = it }
            }
        }
    }

}