package com.example.workouttracker

import java.time.LocalDate
import java.time.LocalTime

enum class ActivityType(val label: String, val kcalPerMin: Int) {
    WALK("Ходьба", 3),
    RUN("Бег", 10),
    YOGA("Йога", 2),
    GYM("Спортзал", 8)
}

data class Training(
    val date: LocalDate,
    val time: LocalTime,
    val durationMin: Int,
    val type: ActivityType
)
