package com.example.playlistmaker.data.settings.impl

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.api.settings.ThemeRepository

class ThemeRepositoryImpl(
    private val settingsSharedPrefs: SharedPreferences,
    private val context: Context
) : ThemeRepository {

    private companion object {
        const val SWITCHER_KEY = "switcher_key"
    }

    override fun getThemeMode(): Int {
       val darkThemeEnabled =
            if (settingsSharedPrefs.contains(SWITCHER_KEY)) settingsSharedPrefs.getBoolean(
                SWITCHER_KEY,
                false
            )
            else isSystemInDarkTheme()
        return if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_NO
    }

    override fun putThemeIntoSharedPrefs(darkThemeEnabled: Boolean) {
        settingsSharedPrefs.edit()
            .putBoolean(SWITCHER_KEY, darkThemeEnabled)
            .apply()
    }

    override fun isSystemInDarkTheme(): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }
}