package com.fauzan.storytelling.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fauzan.storytelling.data.local.entity.StoryEntity
import com.fauzan.storytelling.data.model.StoryModel

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stories: List<StoryEntity>)

    @Query("SELECT * FROM stories")
    fun getStories(): PagingSource<Int, StoryModel>

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}