package com.example.playlistmaker.domain.api

interface ThemeRepository {
    fun isDarkThemeEnabled(isSystemInDarkTheme: Boolean) : Boolean
    fun switchTheme(darkThemeEnabled: Boolean)
}