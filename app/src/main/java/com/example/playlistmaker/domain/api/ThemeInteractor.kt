package com.example.playlistmaker.domain.api

import android.content.res.Resources

interface ThemeInteractor {
    fun getThemeMode() : Int
    fun isSystemInDarkTheme(): Boolean
//    fun switchTheme(darkThemeEnabled: Boolean)

    interface Consumer {
        fun consume(themeMode: Int)
    }
}