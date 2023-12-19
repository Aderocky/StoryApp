package com.example.storyapp_ade.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp_ade.api.response.ListStoryItem
import com.example.storyapp_ade.databinding.ListStoryBinding
import com.example.storyapp_ade.view.detail.DetailActivity

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder.create(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)
        if (user != null) {
            holder.bind(user)
        }
    }

    class MyViewHolder private constructor(private val binding: ListStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(users: ListStoryItem) {
            binding.namaHero.text = users.name

            Glide.with(binding.root.context).load(users.photoUrl).into(binding.gambar)

            itemView.setOnClickListener {
                val storyID = users.id
                val toDetail = Intent(binding.root.context, DetailActivity::class.java)
                toDetail.putExtra(DetailActivity.SEND_ID, storyID)
                binding.root.context.startActivity(toDetail)
            }

        }

        companion object {
            fun create(binding: ListStoryBinding): MyViewHolder {
                return MyViewHolder(binding)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: ListStoryItem, newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
