package com.example.workouttracker

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.LocalDate
import java.time.LocalTime

fun loadTrainings(context: Context): List<Training> {
    val file = File(context.filesDir, "trainings.json")
    if (!file.exists()) return emptyList()
    val text = file.readText()
    val jsonArr = JSONArray(text)
    val list = mutableListOf<Training>()
    for (i in 0 until jsonArr.length()) {
        val obj = jsonArr.getJSONObject(i)
        val date = LocalDate.parse(obj.getString("date"))
        val time = LocalTime.parse(obj.getString("time"))
        val dur = obj.getInt("durationMin")
        val type = ActivityType.valueOf(obj.getString("type"))
        list.add(Training(date, time, dur, type))
    }
    return list
}

fun saveTrainings(context: Context, trainings: List<Training>) {
    val file = File(context.filesDir, "trainings.json")
    val jsonArr = JSONArray()
    trainings.forEach { tr ->
        val obj = JSONObject()
        obj.put("date", tr.date.toString())
        obj.put("time", tr.time.toString())
        obj.put("durationMin", tr.durationMin)
        obj.put("type", tr.type.name)
        jsonArr.put(obj)
    }
    file.writeText(jsonArr.toString())
}