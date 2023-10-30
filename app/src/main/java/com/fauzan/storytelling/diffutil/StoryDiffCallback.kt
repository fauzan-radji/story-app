package com.fauzan.storytelling.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.fauzan.storytelling.data.model.StoryModel

class StoryDiffCallback(private val oldListStory: MutableList<StoryModel>, private val newListStory: List<StoryModel>) : DiffUtil.Callback() {
    override fun getOldListSize() = oldListStory.size
    override fun getNewListSize() = newListStory.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldListStory[oldItemPosition].id == newListStory[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldListStory[oldItemPosition].id == newListStory[newItemPosition].id &&
                oldListStory[oldItemPosition].name == newListStory[newItemPosition].name &&
                oldListStory[oldItemPosition].description == newListStory[newItemPosition].description &&
                oldListStory[oldItemPosition].photoUrl == newListStory[newItemPosition].photoUrl
    }
}
