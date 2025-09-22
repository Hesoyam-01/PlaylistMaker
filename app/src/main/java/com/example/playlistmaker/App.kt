package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.util.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val themeInteractor = Creator.getThemeInteractor(this)
        themeInteractor.getThemeMode(object : ThemeInteractor.ThemeConsumer {
            override fun consume(themeMode: Int) {
                AppCompatDelegate.setDefaultNightMode(themeMode)
            }
        })
    }
}