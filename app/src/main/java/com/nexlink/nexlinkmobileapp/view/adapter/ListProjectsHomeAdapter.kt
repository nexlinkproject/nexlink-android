package com.nexlink.nexlinkmobileapp.view.adapter

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nexlink.nexlinkmobileapp.R
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.ListAllProjectsItem
import com.nexlink.nexlinkmobileapp.databinding.ItemListProjectsHomeBinding
import com.nexlink.nexlinkmobileapp.view.utils.changeBackgroundColor
import com.nexlink.nexlinkmobileapp.view.utils.formatDate

class ListProjectsHomeAdapter :
    ListAdapter<ListAllProjectsItem, ListProjectsHomeAdapter.MyViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemListProjectsHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val review = getItem(position)
        holder.bind(review)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(review)
        }
    }

    class MyViewHolder(private val binding: ItemListProjectsHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(project: ListAllProjectsItem) {
            binding.tvProjectName.text = "${project.name}"
            binding.tvProjectDate.text = formatDate(project.endDate.toString())
            binding.tvProjectStatus.text = project.status

            // Set project status color
            val context = binding.root.context
            val color = when (project.status) {
                "in-progress" -> ContextCompat.getColor(context, R.color.warning_main)
                "completed" -> ContextCompat.getColor(context, R.color.success_main)
                "cancelled" -> ContextCompat.getColor(context, R.color.danger_main)
                else -> ContextCompat.getColor(context, R.color.primary_main)
            }

            changeBackgroundColor(binding.viewProjectStatus, color)
            binding.tvProjectStatus.setTextColor(color)

            // Ubah warna stroke background_item_list_projects
            val background = binding.root.background as LayerDrawable
            val shapeDrawable = background.getDrawable(0) as GradientDrawable
            shapeDrawable.setStroke(1, color)

            when(project.status) {
                "in-progress" -> {
                    binding.tvProjectStatus.text = "In Progress"
                }
                "completed" -> {
                    binding.tvProjectStatus.text = "Completed"
                }
                "cancelled" -> {
                    binding.tvProjectStatus.text = "Cancelled"
                }
                else -> {
                    binding.tvProjectStatus.text = "Active"
                }
            }

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