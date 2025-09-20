package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeInteractorImpl(
    private val repository: ThemeRepository,
) : ThemeInteractor {

    override fun getThemeMode(consumer: ThemeInteractor.ThemeConsumer) {
        consumer.consume(repository.getThemeMode())
    }

    override fun saveTheme(darkThemeEnabled: Boolean) {
        repository.putThemeIntoSharedPrefs(darkThemeEnabled)
    }
}