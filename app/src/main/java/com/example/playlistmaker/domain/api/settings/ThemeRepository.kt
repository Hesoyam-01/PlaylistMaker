package com.example.playlistmaker.domain.api.settings

interface ThemeRepository {
    fun getThemeMode() : Int
    fun putThemeIntoSharedPrefs(darkThemeEnabled: Boolean)
    fun isSystemInDarkTheme(): Boolean
}