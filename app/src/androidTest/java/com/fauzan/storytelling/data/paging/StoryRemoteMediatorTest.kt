package com.fauzan.storytelling.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.fauzan.storytelling.data.local.StoryDatabase
import com.fauzan.storytelling.data.model.StoryModel
import com.fauzan.storytelling.data.remote.response.ErrorResponse
import com.fauzan.storytelling.data.remote.response.LoginResponse
import com.fauzan.storytelling.data.remote.response.StoriesResponse
import com.fauzan.storytelling.data.remote.response.Story
import com.fauzan.storytelling.data.remote.response.StoryResponse
import com.fauzan.storytelling.data.remote.retrofit.ApiService
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@ExperimentalPagingApi
class StoryRemoteMediatorTest {
    private var mockApi: ApiService = FakeApiService()
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(
            database = mockDb,
            apiService = mockApi,
            context = ApplicationProvider.getApplicationContext()
        )
        val pagingState = PagingState<Int, StoryModel>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}

class FakeApiService : ApiService {
    override suspend fun login(email: String, password: String): LoginResponse {
        TODO("Not yet implemented")
    }

    override suspend fun register(name: String, email: String, password: String): ErrorResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getStories(
        token: String, page: Int, limit: Int, location: Int
    ): StoriesResponse {
        val stories: MutableList<Story> = arrayListOf()
        for (i in 0..10) {
            val story = Story(
                id = i.toString(),
                name = "John Doe $i",
                photoUrl = "http://image.com/image",
                description = "Description $i",
                lat = null,
                lon = null,
                createdAt = ""
            )
            stories.add(story)
        }

        return StoriesResponse(
            listStory = stories,
            error = false,
            message = "succcess"
        )
    }

    override suspend fun getStory(token: String, storyId: String): StoryResponse {
        TODO("Not yet implemented")
    }

    override suspend fun addStory(
        token: String, description: RequestBody, photo: MultipartBody.Part
    ): ErrorResponse {
        TODO("Not yet implemented")
    }
}


