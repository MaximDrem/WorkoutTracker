package com.example.workouttracker

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.appbar.MaterialToolbar

class SettingsActivity : AppCompatActivity() {

    private lateinit var btnToggleTheme: Button
    private lateinit var switchNotifications: Switch
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = getSharedPreferences("settings_prefs", MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_mode", false)
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        btnToggleTheme = findViewById(R.id.btnToggleTheme)
        switchNotifications = findViewById(R.id.switchNotifications)

        updateThemeButtonText(isDark)

        btnToggleTheme.setOnClickListener {
            val currentlyDark = prefs.getBoolean("dark_mode", false)
            if (currentlyDark) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                prefs.edit().putBoolean("dark_mode", false).apply()
                updateThemeButtonText(false)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                prefs.edit().putBoolean("dark_mode", true).apply()
                updateThemeButtonText(true)
            }
            recreate()
        }

        val notificationsEnabled = prefs.getBoolean("notifications", true)
        switchNotifications.isChecked = notificationsEnabled
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notifications", isChecked).apply()
        }
    }

    private fun updateThemeButtonText(isDark: Boolean) {
        btnToggleTheme.text = if (isDark) "Отключить" else "Включить"
    }
}
