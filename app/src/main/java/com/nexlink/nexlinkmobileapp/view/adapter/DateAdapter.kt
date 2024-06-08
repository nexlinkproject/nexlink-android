package com.nexlink.nexlinkmobileapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nexlink.nexlinkmobileapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateAdapter(private val dates: List<Date>, private val onDateClickListener: OnDateClickListener) :
    RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    private val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("d", Locale.getDefault())
    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_date_month, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val date = dates[position]
        holder.bind(date)

        val currentPosition = holder.adapterPosition

        holder.itemView.isSelected = (selectedPosition == currentPosition)
        holder.itemView.setBackgroundResource(
            if (selectedPosition == currentPosition) R.drawable.background_item_date_month else R.drawable.background_item_date_month
        )

        val textColor = if (selectedPosition == currentPosition) {
            ContextCompat.getColor(holder.itemView.context, R.color.white)
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.black)
        }

        holder.textDay.setTextColor(textColor)
        holder.textDate.setTextColor(textColor)

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = currentPosition

            if (previousPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousPosition)
            }
            notifyItemChanged(currentPosition)

            onDateClickListener.onDateClick(date)
        }
    }

    override fun getItemCount(): Int = dates.size

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val textDay: TextView = itemView.findViewById(R.id.text_day)
        internal val textDate: TextView = itemView.findViewById(R.id.text_date)

        fun bind(date: Date) {
            textDay.text = dayFormat.format(date).uppercase(Locale.getDefault())
            textDate.text = dateFormat.format(date)
        }
    }

    interface OnDateClickListener {
        fun onDateClick(date: Date)
    }
}