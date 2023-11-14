package com.fauzan.storytelling.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.fauzan.storytelling.DataDummy
import com.fauzan.storytelling.MainDispatcherRule
import com.fauzan.storytelling.data.StoryRepository
import com.fauzan.storytelling.data.model.StoryModel
import com.fauzan.storytelling.data.remote.response.StoriesResponse
import com.fauzan.storytelling.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        val dummyStories = DataDummy.generateDummyStoriesResponse()
        val data: PagingData<StoryModel> = StoryPagingSource.snapshot(dummyStories)
        val expectedStories = MutableLiveData<PagingData<StoryModel>>()
        expectedStories.value = data

        `when`(storyRepository.getStories()).thenReturn(expectedStories)
        val homeViewModel = HomeViewModel(storyRepository)
        val actualStories: PagingData<StoryModel> = homeViewModel.getStories().getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        val storyModels = dummyStories.toStoryModel()
        val snapshot = differ.snapshot()
        Assert.assertNotNull(snapshot)
        Assert.assertEquals(storyModels.size, snapshot.size)
        Assert.assertEquals(storyModels[0], snapshot[0])
    }

    @Test
    fun `when Get Stories Empty Should Return No Data`() = runTest {
        val data: PagingData<StoryModel> = PagingData.from(emptyList())
        val expectedStories = MutableLiveData<PagingData<StoryModel>>()
        expectedStories.value = data
        `when`(storyRepository.getStories()).thenReturn(expectedStories)

        val mainViewModel = HomeViewModel(storyRepository)
        val actualStories: PagingData<StoryModel> = mainViewModel.getStories().getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        Assert.assertEquals(0, differ.snapshot().size)
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<StoryModel>>>() {

    companion object {
        fun snapshot(items: StoriesResponse): PagingData<StoryModel> {
            return PagingData.from(items.toStoryModel())
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryModel>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryModel>>> {
        return LoadResult.Page(
            data = emptyList(),
            prevKey = 0,
            nextKey = 1
        )
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}