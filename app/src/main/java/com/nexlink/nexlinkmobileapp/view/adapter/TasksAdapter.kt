package com.nexlink.nexlinkmobileapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nexlink.nexlinkmobileapp.data.remote.response.tasks.ListAllTasksItem
import com.nexlink.nexlinkmobileapp.databinding.ItemListProjectTasksBinding
import com.nexlink.nexlinkmobileapp.view.utils.formatDate

class TasksAdapter :
    ListAdapter<ListAllTasksItem, TasksAdapter.MyViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemListProjectTasksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val review = getItem(position)
        holder.bind(review)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(review)
        }
    }

    class MyViewHolder(private val binding: ItemListProjectTasksBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: ListAllTasksItem) {
            binding.tvTaskName.text = "${task.title}"
            binding.tvTaskStatus.text = task.status
            binding.tvTaskDate.text = formatDate(task.endDate.toString())
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListAllTasksItem>() {
            override fun areItemsTheSame(
                oldItem: ListAllTasksItem,
                newItem: ListAllTasksItem,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListAllTasksItem,
                newItem: ListAllTasksItem,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListAllTasksItem)
    }
}