package com.example.playlistmaker.presentation.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.domain.api.settings.ThemeInteractor
import com.example.playlistmaker.presentation.search.SearchState
import com.example.playlistmaker.util.Creator

class SettingsViewModel(context: Context) : ViewModel() {
    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                SettingsViewModel(app)
            }
        }
    }
    private val themeInteractor = Creator.provideThemeInteractor(context)

    private val stateLiveData = MutableLiveData<SettingsState>()
    fun observeSettingsState(): LiveData<SettingsState> = stateLiveData

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
}