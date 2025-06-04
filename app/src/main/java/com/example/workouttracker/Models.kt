package com.example.workouttracker

import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.LocalTime

enum class ActivityType(val label: String, val color: Color, val kcalPerMin: Int) {
    WALK("Ходьба", Color(0xFFEF9A9A), 3),
    RUN("Бег", Color(0xFF81C784), 10),
    YOGA("Йога", Color(0xFFFFF59D), 2),
    GYM("Спортзал", Color(0xFF64B5F6), 8)
}

data class Training(
    val date: LocalDate,
    val time: LocalTime,
    val durationMin: Int,
    val type: ActivityType
)