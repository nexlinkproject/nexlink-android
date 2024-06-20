package com.nexlink.nexlinkmobileapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.ListProjectUsersItem
import com.nexlink.nexlinkmobileapp.databinding.ItemTeammatesBinding

class ProjectUsersAdapter(private val showRemoveButton: Boolean) :
    ListAdapter<ListProjectUsersItem, ProjectUsersAdapter.MyViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var onRemoveButtonClickCallback: OnRemoveButtonClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setOnRemoveButtonClickCallback(onRemoveButtonClickCallback: OnRemoveButtonClickCallback) {
        this.onRemoveButtonClickCallback = onRemoveButtonClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemTeammatesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user, showRemoveButton)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(user)
        }
        if (showRemoveButton) {
            holder.binding.btnRemoveTeammate.setOnClickListener {
                onRemoveButtonClickCallback.onRemoveButtonClicked(user)
            }
        }
    }

    class MyViewHolder(val binding: ItemTeammatesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: ListProjectUsersItem, showRemoveButton: Boolean) {
            binding.tvTeammateName.text = user.fullName
            if (showRemoveButton) {
                binding.btnRemoveTeammate.visibility = View.VISIBLE
            } else {
                binding.btnRemoveTeammate.visibility = View.GONE
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListProjectUsersItem>() {
            override fun areItemsTheSame(
                oldItem: ListProjectUsersItem,
                newItem: ListProjectUsersItem,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListProjectUsersItem,
                newItem: ListProjectUsersItem,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListProjectUsersItem)
    }

    interface OnRemoveButtonClickCallback {
        fun onRemoveButtonClicked(data: ListProjectUsersItem)
    }
}