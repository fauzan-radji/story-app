package com.fauzan.storytelling.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fauzan.storytelling.data.local.dao.RemoteKeysDao
import com.fauzan.storytelling.data.local.dao.StoryDao
import com.fauzan.storytelling.data.local.entity.RemoteKeys
import com.fauzan.storytelling.data.local.entity.StoryEntity

@Database(
    entities = [StoryEntity::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        private var INSTANCE: StoryDatabase? = null

        fun getInstance(context: Context): StoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    StoryDatabase::class.java,
                    "story_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}