package com.nexlink.nexlinkmobileapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nexlink.nexlinkmobileapp.data.remote.response.ml.ListDataScheduleItem
import com.nexlink.nexlinkmobileapp.databinding.ItemListProjectTasksBinding
import com.nexlink.nexlinkmobileapp.view.utils.formatDate

class ScheduleMLAdapter :
    ListAdapter<ListDataScheduleItem, ScheduleMLAdapter.MyViewHolder>(DIFF_CALLBACK) {
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
        fun bind(task: ListDataScheduleItem) {
            binding.tvTaskName.text = "${task.name}"
            binding.tvTaskDate.text = formatDate(task.dueDate.toString())

            // Set task status color
            val context = binding.root.context
//            when(task.status) {
//                "pending" -> {
//                    binding.tvTaskStatus.setTextColor(ContextCompat.getColor(context, R.color.warning_main))
//                    binding.tvTaskStatus.text = "Pending"
//                }
//                "done" -> {
//                    binding.tvTaskStatus.setTextColor(ContextCompat.getColor(context, R.color.success_main))
//                    binding.tvTaskStatus.text = "Done"
//                }
//                "cancelled" -> {
//                    binding.tvTaskStatus.setTextColor(ContextCompat.getColor(context, R.color.danger_main))
//                    binding.tvTaskStatus.text = "Cancelled"
//                }
//                else -> {
//                    binding.tvTaskStatus.setTextColor(ContextCompat.getColor(context, R.color.primary_main))
//                    binding.tvTaskStatus.text = "In Progress"
//                }
//            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListDataScheduleItem>() {
            override fun areItemsTheSame(
                oldItem: ListDataScheduleItem,
                newItem: ListDataScheduleItem,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListDataScheduleItem,
                newItem: ListDataScheduleItem,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListDataScheduleItem)
    }
}