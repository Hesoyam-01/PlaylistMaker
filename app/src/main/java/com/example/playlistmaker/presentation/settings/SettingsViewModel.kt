package com.example.playlistmaker.presentation.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.settings.ThemeInteractor
import com.example.playlistmaker.domain.api.sharing.SharingInteractor

class SettingsViewModel(
    private val themeInteractor: ThemeInteractor,
    private val sharingInteractor: SharingInteractor) :
    ViewModel() {

    private val stateLiveData = MutableLiveData<SettingsState>()
    fun observeSettingsState(): LiveData<SettingsState> = stateLiveData

    fun switchTheme(switchOn: Boolean) {
        if (switchOn) renderState(SettingsState.DarkTheme)
        else renderState(SettingsState.LightTheme)
        themeInteractor.saveTheme(switchOn)
    }

    fun getThemeMode() {
        themeInteractor.getThemeMode(object : ThemeInteractor.ThemeConsumer {
            override fun consume(themeMode: Int) {
                val isDarkThemeEnabled = themeMode == AppCompatDelegate.MODE_NIGHT_YES
                if (isDarkThemeEnabled) renderState(SettingsState.DarkTheme)
                else renderState(SettingsState.LightTheme)
            }
        })
    }

    private fun renderState(state: SettingsState) {
        stateLiveData.postValue(state)
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }
}