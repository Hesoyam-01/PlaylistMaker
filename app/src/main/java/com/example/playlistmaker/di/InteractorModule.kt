package com.example.playlistmaker.di

import com.example.playlistmaker.domain.api.main.MainInteractor
import com.example.playlistmaker.domain.api.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.search.SearchInteractor
import com.example.playlistmaker.domain.api.settings.ThemeInteractor
import com.example.playlistmaker.domain.api.sharing.SharingInteractor
import com.example.playlistmaker.domain.impl.main.MainInteractorImpl
import com.example.playlistmaker.domain.impl.search.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.search.SearchInteractorImpl
import com.example.playlistmaker.domain.impl.settings.ThemeInteractorImpl
import com.example.playlistmaker.domain.impl.sharing.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<MainInteractor> {
        MainInteractorImpl(get())
    }

    single<SearchInteractor> {
        SearchInteractorImpl(get())
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    single<ThemeInteractor> {
        ThemeInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }

}