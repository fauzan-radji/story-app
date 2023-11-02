package com.fauzan.storytelling.data.remote.retrofit

import com.fauzan.storytelling.data.remote.response.ErrorResponse
import com.fauzan.storytelling.data.remote.response.LoginResponse
import com.fauzan.storytelling.data.remote.response.StoriesResponse
import com.fauzan.storytelling.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): ErrorResponse

    @GET("stories")
    suspend fun getStories(): StoriesResponse

    @GET("stories/{storyId}")
    suspend fun getStory(@Path("storyId") storyId: String): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part
    ): ErrorResponse
}