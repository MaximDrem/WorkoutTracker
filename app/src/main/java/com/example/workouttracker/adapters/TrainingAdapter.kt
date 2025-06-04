package com.example.workouttracker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.workouttracker.R
import com.example.workouttracker.ActivityType
import com.example.workouttracker.Training
import java.time.format.DateTimeFormatter

class TrainingAdapter(
    private var trainings: MutableList<Training>,
    private val listener: OnDeleteTrainingListener
) : RecyclerView.Adapter<TrainingAdapter.TrainingViewHolder>() {

    interface OnDeleteTrainingListener {
        fun onDeleteTraining(training: Training)
    }

    inner class TrainingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvInfo: TextView = itemView.findViewById(R.id.tvTrainingInfo)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteTraining)

        init {
            btnDelete.setOnClickListener {
                val tr = trainings[bindingAdapterPosition]
                listener.onDeleteTraining(tr)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_training, parent, false)
        return TrainingViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val tr = trainings[position]
        val timeStr = tr.time.format(DateTimeFormatter.ofPattern("HH:mm"))
        val kcal = tr.durationMin * tr.type.kcalPerMin
        holder.tvInfo.text = "• $timeStr | ${tr.type.label} ${tr.durationMin}м ($kcal ккал)"
    }

    override fun getItemCount(): Int = trainings.size

    fun updateList(newList: List<Training>) {
        trainings = newList.toMutableList()
        notifyDataSetChanged()
    }
}
