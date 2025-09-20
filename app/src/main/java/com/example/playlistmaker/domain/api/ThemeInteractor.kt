package com.example.playlistmaker.domain.api

import android.content.res.Resources

interface ThemeInteractor {
    fun getThemeMode(consumer: ThemeConsumer)
    fun saveTheme(darkThemeEnabled: Boolean)

    interface ThemeConsumer {
        fun consume(themeMode: Int)
    }
}