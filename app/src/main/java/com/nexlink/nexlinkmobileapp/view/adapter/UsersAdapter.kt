package com.nexlink.nexlinkmobileapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nexlink.nexlinkmobileapp.data.remote.response.users.ListAllUsersItem
import com.nexlink.nexlinkmobileapp.databinding.ItemListProjectUsersBinding

class UsersAdapter :
    ListAdapter<ListAllUsersItem, UsersAdapter.MyViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemListProjectUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val review = getItem(position)
        holder.bind(review)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(review)
        }
    }

    class MyViewHolder(private val binding: ItemListProjectUsersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: ListAllUsersItem) {
            binding.tvTeammateName.text = "${task.fullName}"
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListAllUsersItem>() {
            override fun areItemsTheSame(
                oldItem: ListAllUsersItem,
                newItem: ListAllUsersItem,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListAllUsersItem,
                newItem: ListAllUsersItem,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListAllUsersItem)
    }
}