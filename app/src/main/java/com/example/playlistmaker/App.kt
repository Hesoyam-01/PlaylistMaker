package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    var darkTheme = false
    private lateinit var settingsSharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        settingsSharedPrefs = getSharedPreferences(SETTINGS_SHARED_PREFS, MODE_PRIVATE)
        if (settingsSharedPrefs.contains(SWITCHER_KEY)) darkTheme =
            settingsSharedPrefs.getBoolean(SWITCHER_KEY, false)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
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