package com.example.workouttracker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
fun WeekCalendarHeader(
    days: List<LocalDate>,
    selected: LocalDate,
    onDateClick: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        days.forEach { date ->
            val isSelected = date == selected
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp)
                    .background(
                        if (isSelected) Color(0xFF424242) else Color.Transparent,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(vertical = 4.dp)
                    .clickable { onDateClick(date) }
            ) {
                Text(
                    text = date.dayOfWeek.name.take(2),
                    color = Color.White,
                    fontSize = 12.sp
                )
                Text(
                    text = date.dayOfMonth.toString(),
                    color = if (isSelected) Color(0xFFFF9800) else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun StatsChart(
    trainings: List<Training>,
    weekDays: List<LocalDate>,
    highlightedDay: LocalDate?,
    onBarClick: (LocalDate) -> Unit
) {
    val caloriesPerDay = weekDays.map { day ->
        trainings.filter { it.date == day }
            .sumOf { it.durationMin * it.type.kcalPerMin }
    }
    val maxCalories = caloriesPerDay.maxOrNull() ?: 0

    val barWidth = 24.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = barWidth / 2)
            .height(180.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        weekDays.forEachIndexed { index, day ->
            val totalKcal = caloriesPerDay[index]
            val ratio = if (maxCalories == 0) 0f else totalKcal.toFloat() / maxCalories
            val isHighlighted = day == highlightedDay
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onBarClick(day) }
                    .padding(vertical = 4.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .width(barWidth)
                        .height(140.dp)
                ) {
                    val barHeight = size.height * ratio
                    drawRect(
                        color = when {
                            isHighlighted -> Color.Cyan
                            totalKcal == 0 -> Color.Gray
                            else -> Color(0xFFEF9A9A)
                        },
                        topLeft = Offset(0f, size.height - barHeight),
                        size = androidx.compose.ui.geometry.Size(size.width, barHeight)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = day.dayOfWeek.name.take(2),
                    fontSize = 12.sp,
                    color = if (isHighlighted) Color.Cyan else Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun TrainingDayCard(date: LocalDate, trainings: List<Training>) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151515)),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = "Тренировки ${date.dayOfMonth} ${monthRu(date.monthValue)}",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            trainings.forEach { tr ->
                val kcal = tr.durationMin * tr.type.kcalPerMin
                Text(
                    text = "• ${tr.time} | ${tr.type.label} ${tr.durationMin}м (${kcal} ккал)",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}

fun monthRu(m: Int) = listOf(
    "января", "февраля", "марта", "апреля", "мая", "июня",
    "июля", "августа", "сентября", "октября", "ноября", "декабря"
)[m - 1]