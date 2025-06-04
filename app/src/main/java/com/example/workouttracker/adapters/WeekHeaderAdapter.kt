package com.example.workouttracker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.workouttracker.R
import java.time.LocalDate

class WeekHeaderAdapter(
    private var days: List<LocalDate>,
    private var selectedDate: LocalDate,
    private val listener: OnDateClickListener
) : RecyclerView.Adapter<WeekHeaderAdapter.DayViewHolder>() {

    interface OnDateClickListener {
        fun onDateClick(date: LocalDate)
    }

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDayOfWeek: TextView = itemView.findViewById(R.id.tvDayOfWeek)
        val tvDayOfMonth: TextView = itemView.findViewById(R.id.tvDayOfMonth)

        init {
            itemView.setOnClickListener {
                val date = days[bindingAdapterPosition]
                listener.onDateClick(date)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_week_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val date = days[position]
        val context = holder.itemView.context

        holder.tvDayOfWeek.text = date.dayOfWeek.name.take(2)
        holder.tvDayOfMonth.text = date.dayOfMonth.toString()

        val isSelected = date == selectedDate

        if (isSelected) {
            holder.itemView.setBackgroundResource(R.drawable.bg_selected_day)
        } else {
            holder.itemView.setBackgroundResource(android.R.color.transparent)
        }

        val colorDayNumber = if (isSelected) {
            ContextCompat.getColor(context, R.color.calendarSelectedText)
        } else {
            ContextCompat.getColor(context, R.color.textPrimary)
        }
        holder.tvDayOfMonth.setTextColor(colorDayNumber)

        holder.tvDayOfWeek.setTextColor(
            ContextCompat.getColor(context, R.color.textPrimary)
        )
    }

    override fun getItemCount(): Int = days.size

    fun updateWeek(newDays: List<LocalDate>, newSelected: LocalDate) {
        days = newDays
        selectedDate = newSelected
        notifyDataSetChanged()
    }
}
