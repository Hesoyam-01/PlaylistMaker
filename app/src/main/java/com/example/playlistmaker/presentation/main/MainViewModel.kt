package com.example.playlistmaker.presentation.main

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.main.MainInteractor

class MainViewModel(private val mainInteractor: MainInteractor) : ViewModel() {

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