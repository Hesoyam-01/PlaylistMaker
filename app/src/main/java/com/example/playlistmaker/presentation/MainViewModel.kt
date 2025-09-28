package com.example.playlistmaker.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.util.Creator

class MainViewModel(context: Context) : ViewModel() {
    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                MainViewModel(app)
            }
        }
    }
    private val mainInteractor = Creator.provideMainInteractor(context)

    fun openSearch() {
        mainInteractor.openSearch()
    }

    fun openLibrary() {
        mainInteractor.openLibrary()
    }

    fun openSettings() {
        mainInteractor.openSettings()
    }
}