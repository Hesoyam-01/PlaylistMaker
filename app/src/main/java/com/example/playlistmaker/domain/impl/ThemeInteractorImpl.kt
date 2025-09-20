package com.example.playlistmaker.domain.impl

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeInteractorImpl(
    private val repository: ThemeRepository,
    private val context: Context
) : ThemeInteractor {
    override fun getThemeMode() : Int {
        val darkThemeEnabled = repository.isDarkThemeEnabled(isSystemInDarkTheme())
        return if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_NO
    }

    override fun isSystemInDarkTheme(): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    override fun switchTheme(darkThemeEnabled: Boolean) {
        val darkTheme = repository.isDarkThemeEnabled(isSystemInDarkTheme())
    }


}