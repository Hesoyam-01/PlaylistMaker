package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.SearchResult
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.TracksInteractor

class App : Application(), ThemeInteractor.ThemeConsumer {
    //    var darkTheme = false
    private val themeInteractor = Creator.getThemeInteractor(this)

    override fun onCreate() {
        super.onCreate()
        themeInteractor.getThemeMode(this)
//        applyThemeSettings()
    }

    override fun consume(themeMode: Int) {
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }


    /*private fun applyThemeSettings() {
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
    }*/


    /*private fun isSystemInDarkTheme(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }*/

    /*fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        settingsSharedPrefs.edit()
            .putBoolean(SWITCHER_KEY, darkTheme)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

    }*/

    /*private companion object {
        const val SETTINGS_SHARED_PREFS = "settings_shared_prefs"
        const val SWITCHER_KEY = "switcher_key"
    }*/
}