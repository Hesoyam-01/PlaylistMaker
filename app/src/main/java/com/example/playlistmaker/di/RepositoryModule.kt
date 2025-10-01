package com.example.playlistmaker.di

import com.example.playlistmaker.data.impl.search.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.impl.search.SearchRepositoryImpl
import com.example.playlistmaker.data.impl.settings.ThemeRepositoryImpl
import com.example.playlistmaker.domain.api.search.SearchHistoryRepository
import com.example.playlistmaker.domain.api.search.SearchRepository
import com.example.playlistmaker.domain.api.settings.ThemeRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {

    single<SearchRepository> {
        SearchRepositoryImpl(get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(named("searchHistoryStorage")))
    }

    single<ThemeRepository> {
        ThemeRepositoryImpl(get(named("themeStorage")), androidContext())
    }

}