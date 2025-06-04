package com.example.workouttracker

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun MainScreen() {
    val context = LocalContext.current

    var trainings by remember { mutableStateOf(listOf<Training>()) }

    LaunchedEffect(Unit) {
        val file = File(context.filesDir, "trainings.json")
        if (!file.exists()) {
            val initList = listOf(
                Training(
                    date = LocalDate.now().minusDays(2),
                    time = LocalTime.of(9, 0),
                    durationMin = 30,
                    type = ActivityType.RUN
                ),
                Training(
                    date = LocalDate.now().minusDays(1),
                    time = LocalTime.of(18, 0),
                    durationMin = 45,
                    type = ActivityType.WALK
                ),
                Training(
                    date = LocalDate.now().minusDays(1),
                    time = LocalTime.of(20, 0),
                    durationMin = 60,
                    type = ActivityType.GYM
                )
            )
            saveTrainings(context, initList)
        }
        trainings = loadTrainings(context)
    }

    val today = LocalDate.now()
    val initialWeekStart = remember {
        today.minusDays((today.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong())
    }

    var selectedWeekStart by remember { mutableStateOf(initialWeekStart) }
    var selectedDate by remember { mutableStateOf(today) }

    val weekDays = (0..6).map { selectedWeekStart.plusDays(it.toLong()) }

    var showDialog by remember { mutableStateOf(false) }
    var newType by remember { mutableStateOf(ActivityType.WALK) }
    var newDuration by remember { mutableStateOf(30) }
    var newDate by remember { mutableStateOf(LocalDate.now()) }
    var newTime by remember { mutableStateOf(LocalTime.now().withMinute(0).withSecond(0).withNano(0)) }

    var highlightedDay by remember { mutableStateOf<LocalDate?>(null) }
    var showDeleteList by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF202020))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { /* TODO: Open settings */ }) {
                Text("Настройки", fontSize = 14.sp)
            }
            Button(onClick = {
                selectedWeekStart = selectedWeekStart.minusWeeks(1)
                selectedDate = selectedWeekStart
                highlightedDay = null
                showDeleteList = false
            }) {
                Text("‹ Неделя", fontSize = 14.sp)
            }
            Button(onClick = {
                selectedWeekStart = selectedWeekStart.plusWeeks(1)
                selectedDate = selectedWeekStart
                highlightedDay = null
                showDeleteList = false
            }) {
                Text("Неделя ›", fontSize = 14.sp)
            }
        }

        WeekCalendarHeader(
            days = weekDays,
            selected = selectedDate,
            onDateClick = { clickedDate ->
                selectedDate = clickedDate
                highlightedDay = null
                showDeleteList = false
            }
        )

        Spacer(Modifier.height(12.dp))

        val trainingsForSelected = trainings.filter { it.date == selectedDate }
        if (trainingsForSelected.isEmpty()) {
            Text(
                text = "Нет тренировок на ${selectedDate.dayOfMonth} ${monthRu(selectedDate.monthValue)}",
                color = Color(0xFFBBBBBB),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                textAlign = TextAlign.Center
            )
        } else {
            Column(modifier = Modifier.fillMaxWidth()) {
                TrainingDayCard(
                    date = selectedDate,
                    trainings = trainingsForSelected
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { showDeleteList = true },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Удалить")
                }
            }
        }

        Spacer(modifier = Modifier.fillMaxHeight(0.25f))

        StatsChart(
            trainings = trainings,
            weekDays = weekDays,
            highlightedDay = highlightedDay,
            onBarClick = { day ->
                highlightedDay = if (highlightedDay == day) null else day
                selectedDate = day
                showDeleteList = false
            }
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                newType = ActivityType.WALK
                newDuration = 30
                newDate = LocalDate.now()
                newTime = LocalTime.now().withMinute(0).withSecond(0).withNano(0)
                showDialog = true
                showDeleteList = false
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Добавить тренировку")
        }
    }

    if (showDeleteList) {
        Dialog(onDismissRequest = { showDeleteList = false }) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF303030)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Удалить тренировки ${selectedDate.dayOfMonth} ${monthRu(selectedDate.monthValue)}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Divider(color = Color.Gray, thickness = 1.dp)
                    Spacer(Modifier.height(8.dp))
                    val toDeleteList = trainings.filter { it.date == selectedDate }
                    LazyColumn {
                        itemsIndexed(toDeleteList) { index, tr ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = buildString {
                                        append("${tr.time.hour}:")
                                        append(tr.time.minute.toString().padStart(2, '0'))
                                        append(" | ${tr.type.label} ${tr.durationMin}м")
                                        val kcal = tr.durationMin * tr.type.kcalPerMin
                                        append(" (${kcal} ккал)")
                                    },
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                TextButton(onClick = {
                                    trainings = trainings.toMutableList().also { list ->
                                        list.removeAll { it == tr }
                                    }
                                    saveTrainings(context, trainings)
                                    if (trainings.none { it.date == selectedDate }) {
                                        showDeleteList = false
                                    }
                                }) {
                                    Text("Удалить", color = Color.Red)
                                }
                            }
                            Divider(color = Color.DarkGray, thickness = 0.5.dp)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { showDeleteList = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Закрыть")
                    }
                }
            }
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF303030)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextButton(onClick = {
                            newType = when (newType) {
                                ActivityType.WALK -> ActivityType.RUN
                                ActivityType.RUN -> ActivityType.YOGA
                                ActivityType.YOGA -> ActivityType.GYM
                                ActivityType.GYM -> ActivityType.WALK
                            }
                        }) {
                            Text("Тип: ${newType.label}", color = Color.White)
                        }
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = {
                            val options = listOf(15, 30, 45, 60, 90, 120)
                            val idx = options.indexOf(newDuration).takeIf { it >= 0 } ?: 1
                            newDuration = options[(idx + 1) % options.size]
                        }) {
                            Text("Длительность: ${newDuration}м", color = Color.White)
                        }
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = {
                            val year = newDate.year
                            val month = newDate.monthValue - 1
                            val day = newDate.dayOfMonth
                            DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    newDate = LocalDate.of(y, m + 1, d)
                                    TimePickerDialog(
                                        context,
                                        { _, hour, minute ->
                                            newTime = LocalTime.of(hour, minute)
                                        },
                                        newTime.hour,
                                        newTime.minute,
                                        true
                                    ).show()
                                },
                                year,
                                month,
                                day
                            ).show()
                        }) {
                            Text(
                                "Дата: ${newDate.dayOfMonth}.${newDate.monthValue}  " +
                                        "${newTime.hour}:${newTime.minute.toString().padStart(2, '0')}",
                                color = Color.White
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Отмена", color = Color.White)
                        }
                        TextButton(onClick = {
                            val newTraining = Training(
                                date = newDate,
                                time = newTime,
                                durationMin = newDuration,
                                type = newType
                            )
                            trainings = trainings + newTraining
                            saveTrainings(context, trainings)
                            showDialog = false
                        }) {
                            Text("Сохранить", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}