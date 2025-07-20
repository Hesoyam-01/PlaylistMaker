package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    var darkTheme = false
    private lateinit var settingsSharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        applyThemeSettings()
    }

    private fun applyThemeSettings() {
        settingsSharedPrefs = getSharedPreferences(SETTINGS_SHARED_PREFS, MODE_PRIVATE)

        when {
            settingsSharedPrefs.contains(SWITCHER_KEY) -> {
                darkTheme = settingsSharedPrefs.getBoolean(SWITCHER_KEY, false)
                val mode = if (darkTheme) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
                AppCompatDelegate.setDefaultNightMode(mode)
            }
            else -> {
                darkTheme = isSystemInDarkTheme()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }


    private fun isSystemInDarkTheme(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        settingsSharedPrefs.edit()
            .putBoolean(SWITCHER_KEY, darkTheme)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

    }

    private companion object {
        const val SETTINGS_SHARED_PREFS = "settings_shared_prefs"
        const val SWITCHER_KEY = "switcher_key"
    }
}