package com.example.workouttracker.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.workouttracker.R
import com.example.workouttracker.Training
import java.time.LocalDate

class ChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var trainings: List<Training> = emptyList()
    private var weekDays: List<LocalDate> = emptyList()
    private var highlightedDay: LocalDate? = null

    private val paintBar = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 30f
        color = ContextCompat.getColor(context, R.color.textPrimary)
        textAlign = Paint.Align.CENTER
    }

    fun setData(
        allTrainings: List<Training>,
        weekDays: List<LocalDate>,
        highlighted: LocalDate?
    ) {
        this.trainings = allTrainings
        this.weekDays = weekDays
        this.highlightedDay = highlighted
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (weekDays.isEmpty()) return

        val caloriesPerDay = weekDays.map { day ->
            trainings.filter { it.date == day }
                .sumOf { it.durationMin * it.type.kcalPerMin }
        }
        val maxCalories = caloriesPerDay.maxOrNull() ?: 0

        val barCount = weekDays.size
        val totalWidth = width.toFloat()
        val totalHeight = height.toFloat()
        val barWidth = totalWidth / (barCount * 2)

        for ((index, day) in weekDays.withIndex()) {
            val cal = caloriesPerDay[index]
            val ratio = if (maxCalories == 0) 0f else cal.toFloat() / maxCalories
            val barHeight = totalHeight * ratio * 0.8f

            val colorBar = when {
                day == highlightedDay -> R.color.chartBarHighlight
                cal == 0 -> R.color.chartBarEmpty
                else -> R.color.chartBarDefault
            }
            paintBar.color = ContextCompat.getColor(context, colorBar)

            val left = index * 2 * barWidth + barWidth / 2
            val top = totalHeight - barHeight - 40f
            val right = left + barWidth
            val bottom = totalHeight - 40f

            canvas.drawRect(left, top, right, bottom, paintBar)

            val dowText = day.dayOfWeek.name.take(2)
            canvas.drawText(
                dowText,
                left + barWidth / 2,
                totalHeight - 10f,
                paintText
            )
        }
    }
}
