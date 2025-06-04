package com.example.workouttracker

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workouttracker.adapters.TrainingAdapter
import com.example.workouttracker.ui.ChartView
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale
import android.view.View


class MainActivity : AppCompatActivity(),
    TrainingAdapter.OnDeleteTrainingListener {

    private lateinit var btnOpenSettings: Button
    private lateinit var btnPrevWeek: Button
    private lateinit var btnNextWeek: Button
    private lateinit var llWeekHeader: LinearLayout

    private lateinit var rvTrainings: RecyclerView
    private lateinit var tvNoTrainings: TextView
    private lateinit var btnDeleteTrainings: Button
    private lateinit var btnAddTraining: Button
    private lateinit var chartView: ChartView

    private lateinit var trainingAdapter: TrainingAdapter

    private var trainings: MutableList<Training> = mutableListOf()

    private val today: LocalDate = LocalDate.now()
    private var selectedWeekStart: LocalDate = computeWeekStart(today)
    private var selectedDate: LocalDate = today

    private var newType: ActivityType = ActivityType.WALK
    private var newDuration: Int = 30
    private var newDate: LocalDate = today
    private var newTime: LocalTime = LocalTime.now().withMinute(0).withSecond(0).withNano(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnOpenSettings = findViewById(R.id.btnOpenSettings)
        btnPrevWeek = findViewById(R.id.btnPrevWeek)
        btnNextWeek = findViewById(R.id.btnNextWeek)
        llWeekHeader = findViewById(R.id.llWeekHeader)

        rvTrainings = findViewById(R.id.rvTrainings)
        tvNoTrainings = findViewById(R.id.tvNoTrainings)
        btnDeleteTrainings = findViewById(R.id.btnDeleteTrainings)
        btnAddTraining = findViewById(R.id.btnAddTraining)
        chartView = findViewById(R.id.chartView)

        trainings = loadTrainings(this).toMutableList()

        trainingAdapter = TrainingAdapter(mutableListOf(), this)
        rvTrainings.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = trainingAdapter
        }

        populateWeekHeader()

        updateTrainingsForSelectedDate()
        updateChart()

        btnOpenSettings = findViewById(R.id.btnOpenSettings)
        btnOpenSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }


        btnPrevWeek.setOnClickListener {
            selectedWeekStart = selectedWeekStart.minusWeeks(1)
            selectedDate = selectedWeekStart
            populateWeekHeader()
            updateTrainingsForSelectedDate()
            updateChart()
        }

        btnNextWeek.setOnClickListener {
            selectedWeekStart = selectedWeekStart.plusWeeks(1)
            selectedDate = selectedWeekStart
            populateWeekHeader()
            updateTrainingsForSelectedDate()
            updateChart()
        }

        btnAddTraining.setOnClickListener {
            showAddTrainingDialog()
        }

        btnDeleteTrainings.setOnClickListener {
            trainings.removeAll { it.date == selectedDate }
            saveTrainings(this, trainings)
            updateTrainingsForSelectedDate()
            updateChart()
            it.visibility = View.GONE
        }
    }


    private fun populateWeekHeader() {
        llWeekHeader.removeAllViews()

        val weekDays = generateWeekDays(selectedWeekStart)

        for (day in weekDays) {
            val dayView = LayoutInflater.from(this)
                .inflate(R.layout.item_week_day, llWeekHeader, false) as LinearLayout

            val tvDOW = dayView.findViewById<TextView>(R.id.tvDayOfWeek)
            val tvDOM = dayView.findViewById<TextView>(R.id.tvDayOfMonth)

            val dowShort = day.dayOfWeek
                .getDisplayName(TextStyle.SHORT, Locale("ru"))
                .uppercase(Locale("ru"))
                .take(2)
            tvDOW.text = dowShort

            tvDOM.text = day.dayOfMonth.toString()

            if (day == selectedDate) {
                dayView.setBackgroundResource(R.drawable.bg_selected_day)
                tvDOM.setTextColor(ContextCompat.getColor(this, R.color.calendarSelectedText))
            } else {
                dayView.setBackgroundResource(android.R.color.transparent)
                tvDOM.setTextColor(ContextCompat.getColor(this, R.color.textPrimary))
            }
            tvDOW.setTextColor(ContextCompat.getColor(this, R.color.textPrimary))

            val params = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
            params.weight = 1f
            dayView.layoutParams = params

            dayView.setOnClickListener {
                selectedDate = day
                populateWeekHeader()
                updateTrainingsForSelectedDate()
                updateChart()
            }

            llWeekHeader.addView(dayView)
        }
    }

    private fun computeWeekStart(date: LocalDate): LocalDate {
        val dow = date.dayOfWeek.value
        return date.minusDays((dow - DayOfWeek.MONDAY.value).toLong())
    }

    private fun generateWeekDays(weekStart: LocalDate): List<LocalDate> {
        return (0 until 7).map { weekStart.plusDays(it.toLong()) }
    }

    private fun updateTrainingsForSelectedDate() {
        val trainingsForDate = trainings.filter { it.date == selectedDate }
        if (trainingsForDate.isEmpty()) {
            tvNoTrainings.text = "Нет тренировок на ${selectedDate.dayOfMonth} ${monthRu(selectedDate.monthValue)}"
            tvNoTrainings.visibility = View.VISIBLE
            rvTrainings.visibility = View.GONE
            btnDeleteTrainings.visibility = View.GONE
        } else {
            tvNoTrainings.visibility = View.GONE
            rvTrainings.visibility = View.VISIBLE
            trainingAdapter.updateList(trainingsForDate)
            btnDeleteTrainings.visibility = View.VISIBLE
        }
    }


    private fun updateChart() {
        val weekDays = generateWeekDays(selectedWeekStart)
        chartView.setData(trainings, weekDays, selectedDate)
    }


    private fun showAddTrainingDialog() {
        val dpd = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                newDate = LocalDate.of(year, month + 1, dayOfMonth)

                val tpd = TimePickerDialog(
                    this,
                    { _, hour, minute ->
                        newTime = LocalTime.of(hour, minute)
                        showTypeDurationDialog()
                    },
                    newTime.hour,
                    newTime.minute,
                    true
                )
                tpd.show()
            },
            newDate.year,
            newDate.monthValue - 1,
            newDate.dayOfMonth
        )
        dpd.show()
    }


    private fun showTypeDurationDialog() {
        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_type_duration, null)

        val spinnerType = dialogView.findViewById<android.widget.Spinner>(R.id.spinnerType)
        val spinnerDuration = dialogView.findViewById<android.widget.Spinner>(R.id.spinnerDuration)

        val types = ActivityType.values().map { it.label }
        spinnerType.adapter = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            types
        )

        val durations = listOf(15, 30, 45, 60, 90, 120).map { "$it мин" }
        spinnerDuration.adapter = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            durations
        )

        spinnerType.setSelection(types.indexOf(newType.label).takeIf { it >= 0 } ?: 0)
        spinnerDuration.setSelection(
            durations.indexOf("${newDuration} мин").takeIf { it >= 0 } ?: 1
        )

        AlertDialog.Builder(this)
            .setTitle("Новая тренировка")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val selectedLabel = spinnerType.selectedItem as String
                newType = ActivityType.values().first { it.label == selectedLabel }

                val durLabel = spinnerDuration.selectedItem as String
                newDuration = durLabel.split(" ")[0].toInt()

                val newTraining = Training(newDate, newTime, newDuration, newType)
                trainings.add(newTraining)
                saveTrainings(this, trainings)

                updateTrainingsForSelectedDate()
                updateChart()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDeleteTraining(training: Training) {
        trainings.remove(training)
        saveTrainings(this, trainings)
        updateTrainingsForSelectedDate()
        updateChart()
    }

    private fun monthRu(m: Int) = listOf(
        "января", "февраля", "марта", "апреля", "мая", "июня",
        "июля", "августа", "сентября", "октября", "ноября", "декабря"
    )[m - 1]
}
