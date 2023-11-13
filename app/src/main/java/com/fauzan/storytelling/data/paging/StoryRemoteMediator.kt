package com.fauzan.storytelling.data.paging

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.fauzan.storytelling.data.datastore.UserPreference
import com.fauzan.storytelling.data.datastore.dataStore
import com.fauzan.storytelling.data.local.StoryDatabase
import com.fauzan.storytelling.data.local.entity.RemoteKeys
import com.fauzan.storytelling.data.model.StoryModel
import com.fauzan.storytelling.data.remote.retrofit.ApiService

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    context: Context
) : RemoteMediator<Int, StoryModel>() {
    private val userPreference = UserPreference.getInstance(context.dataStore)

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryModel>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeyById(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryModel>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeyById(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, StoryModel>
    ): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeyById(id)
            }
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryModel>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)

                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)

                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)

                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)

                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)

                nextKey
            }
        }

        try {
            val stories = apiService.getStories(
                token = userPreference.getToken(),
                page = page,
                limit = state.config.pageSize
            ).toStoryEntity()

            val endOfPaginationReached = stories.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.storyDao().deleteAll()
                    database.remoteKeysDao().deleteAll()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = stories.map { story ->
                    RemoteKeys(id = story.id, prevKey = prevKey, nextKey = nextKey)
                }

                database.remoteKeysDao().insertAll(keys)
                database.storyDao().insertAll(stories)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}