package com.nexlink.nexlinkmobileapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nexlink.nexlinkmobileapp.R
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.ListAllProjectsItem
import com.nexlink.nexlinkmobileapp.databinding.ItemListProjectBinding
import com.nexlink.nexlinkmobileapp.view.utils.changeBackgroundColor
import com.nexlink.nexlinkmobileapp.view.utils.formatDate

class ProjectsAdapter :
    ListAdapter<ListAllProjectsItem, ProjectsAdapter.MyViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemListProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val review = getItem(position)
        holder.bind(review)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(review)
        }
    }

    class MyViewHolder(private val binding: ItemListProjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(project: ListAllProjectsItem) {
            binding.tvProjectName.text = "${project.name}"
            binding.tvProjectDate.text = formatDate(project.endDate.toString())

            // Set project status color
            val context = binding.root.context
            val color = when (project.status) {
                "in-progress" -> ContextCompat.getColor(context, R.color.warning_main)
                "completed" -> ContextCompat.getColor(context, R.color.success_main)
                "cancelled" -> ContextCompat.getColor(context, R.color.danger_main)
                else -> ContextCompat.getColor(context, R.color.primary_main)
            }

            changeBackgroundColor(binding.viewProjectStatus, color)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListAllProjectsItem>() {
            override fun areItemsTheSame(
                oldItem: ListAllProjectsItem,
                newItem: ListAllProjectsItem,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListAllProjectsItem,
                newItem: ListAllProjectsItem,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListAllProjectsItem)
    }
}