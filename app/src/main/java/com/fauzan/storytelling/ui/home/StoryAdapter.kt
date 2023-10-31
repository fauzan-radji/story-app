package com.fauzan.storytelling.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fauzan.storytelling.data.model.StoryModel
import com.fauzan.storytelling.databinding.PostItemRowBinding
import com.fauzan.storytelling.diffutil.StoryDiffCallback

class StoryAdapter(private val listStory: MutableList<StoryModel>, private val listener: (StoryModel, PostItemRowBinding) -> Unit) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    fun updateData(newListStory: List<StoryModel>) {
        val diffCalback = StoryDiffCallback(listStory, newListStory)
        val diffResult = DiffUtil.calculateDiff(diffCalback)
        listStory.clear()
        listStory.addAll(newListStory)
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(private val binding: PostItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryModel, listener: (StoryModel, PostItemRowBinding) -> Unit) {
            with(binding) {
                tvTitle.text = story.name
                tvDescription.text = story.description
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .into(binding.ivThumbnail)
                ivThumbnail.transitionName = "thumbnail_${story.id}"
                tvTitle.transitionName = "title_${story.id}"
                tvDescription.transitionName = "description_${story.id}"
                itemView.setOnClickListener {
                    listener(story, binding)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PostItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listStory.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listStory[position], listener)
    }
}