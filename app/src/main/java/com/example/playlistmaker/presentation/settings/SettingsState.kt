package com.example.playlistmaker.presentation.settings

sealed interface SettingsState {
    data object LightTheme : SettingsState
    data object DarkTheme : SettingsState
}