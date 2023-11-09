package com.fauzan.storytelling.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fauzan.storytelling.data.model.StoryModel
import com.fauzan.storytelling.databinding.PostItemRowBinding

class StoryAdapter(private val listener: (StoryModel, PostItemRowBinding) -> Unit) : PagingDataAdapter<StoryModel, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(private val binding: PostItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryModel, listener: (StoryModel, PostItemRowBinding) -> Unit) {
            with(binding) {
                tvTitle.text = story.name
                tvDescription.text = story.description
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .into(ivThumbnail)
                ivThumbnail.transitionName = "thumbnail_${story.id}"
                tvTitle.transitionName = "title_${story.id}"
                tvDescription.transitionName = "description_${story.id}"
                itemView.setOnClickListener {
                    listener(story, this)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PostItemRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null)
            holder.bind(data, listener)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryModel>() {
            override fun areItemsTheSame(oldItem: StoryModel, newItem: StoryModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryModel, newItem: StoryModel): Boolean {
                return oldItem.id == newItem.id &&
                        oldItem.name == newItem.name &&
                        oldItem.description == newItem.description &&
                        oldItem.photoUrl == newItem.photoUrl
            }
        }
    }
}