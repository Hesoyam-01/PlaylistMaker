package com.example.playlistmaker.domain.impl.settings

import com.example.playlistmaker.domain.api.settings.ThemeInteractor
import com.example.playlistmaker.domain.api.settings.ThemeRepository

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