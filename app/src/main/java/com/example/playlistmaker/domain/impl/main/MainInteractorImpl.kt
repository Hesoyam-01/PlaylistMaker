package com.example.playlistmaker.domain.impl.main

import com.example.playlistmaker.domain.api.main.MainInteractor
import com.example.playlistmaker.domain.api.main.MainNavigator

class MainInteractorImpl(private val mainNavigator: MainNavigator) : MainInteractor {
    override fun openSearch() {
        mainNavigator.openSearch()
    }

    override fun openLibrary() {
        mainNavigator.openLibrary()
    }

    override fun openSettings() {
        mainNavigator.openSettings()
    }
}