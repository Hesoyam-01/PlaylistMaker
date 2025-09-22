package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.SearchResult
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.TracksInteractor

class App : Application(), ThemeInteractor.ThemeConsumer {

    override fun onCreate() {
        super.onCreate()
        val themeInteractor = Creator.getThemeInteractor(this)
        themeInteractor.getThemeMode(this)
    }

    override fun consume(themeMode: Int) {
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }

}