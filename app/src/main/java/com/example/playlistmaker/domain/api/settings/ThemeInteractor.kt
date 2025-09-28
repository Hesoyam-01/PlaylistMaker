package com.example.playlistmaker.domain.api.settings

interface ThemeInteractor {
    fun getThemeMode(consumer: ThemeConsumer)
    fun saveTheme(darkThemeEnabled: Boolean)

    interface ThemeConsumer {
        fun consume(themeMode: Int)
    }
}