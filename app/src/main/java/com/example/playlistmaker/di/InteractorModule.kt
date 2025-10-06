package com.example.playlistmaker.di

import com.example.playlistmaker.domain.api.main.MainInteractor
import com.example.playlistmaker.domain.api.player.MediaInteractor
import com.example.playlistmaker.domain.api.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.search.SearchInteractor
import com.example.playlistmaker.domain.api.settings.ThemeInteractor
import com.example.playlistmaker.domain.api.sharing.SharingInteractor
import com.example.playlistmaker.domain.impl.MediaInteractorImpl
import com.example.playlistmaker.domain.impl.main.MainInteractorImpl
import com.example.playlistmaker.domain.impl.search.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.search.SearchInteractorImpl
import com.example.playlistmaker.domain.impl.settings.ThemeInteractorImpl
import com.example.playlistmaker.domain.impl.sharing.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    factory<MainInteractor> {
        MainInteractorImpl(get())
    }

    factory<SearchInteractor> {
        SearchInteractorImpl(get())
    }

    factory<MediaInteractor> {
        MediaInteractorImpl(get())
    }

    factory<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    factory<ThemeInteractor> {
        ThemeInteractorImpl(get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(get())
    }

}