package com.example.playlistmaker.data

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeRepositoryImpl(private val settingsSharedPrefs: SharedPreferences) : ThemeRepository {

    private companion object {
        const val SWITCHER_KEY = "switcher_key"
    }

    private var darkThemeEnabled = false

    override fun isDarkThemeEnabled(isSystemInDarkTheme: Boolean): Boolean {
        darkThemeEnabled = if (settingsSharedPrefs.contains(SWITCHER_KEY)) settingsSharedPrefs.getBoolean(
            SWITCHER_KEY,
            false
        )
        else isSystemInDarkTheme
        return darkThemeEnabled
    }

    override fun switchTheme(darkThemeEnabled: Boolean) {
        this.darkThemeEnabled = darkThemeEnabled

        settingsSharedPrefs.edit()
            .putBoolean(SWITCHER_KEY, this.darkThemeEnabled)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}