package com.example.playlistmaker.util

import android.content.Context
import com.example.playlistmaker.data.impl.search.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.impl.settings.ThemeRepositoryImpl
import com.example.playlistmaker.data.impl.search.TracksRepositoryImpl
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.impl.main.MainNavigatorImpl
import com.example.playlistmaker.data.impl.sharing.ExternalNavigatorImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.SearchAPI
import com.example.playlistmaker.data.storage.PrefsStorageClient
import com.example.playlistmaker.domain.api.main.MainInteractor
import com.example.playlistmaker.domain.api.main.MainNavigator
import com.example.playlistmaker.domain.api.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.search.SearchHistoryRepository
import com.example.playlistmaker.domain.api.settings.ThemeInteractor
import com.example.playlistmaker.domain.api.settings.ThemeRepository
import com.example.playlistmaker.domain.api.search.TracksInteractor
import com.example.playlistmaker.domain.api.search.TracksRepository
import com.example.playlistmaker.domain.api.sharing.ExternalNavigator
import com.example.playlistmaker.domain.api.sharing.SharingInteractor
import com.example.playlistmaker.domain.impl.main.MainInteractorImpl
import com.example.playlistmaker.domain.impl.search.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.settings.ThemeInteractorImpl
import com.example.playlistmaker.domain.impl.search.TracksInteractorImpl
import com.example.playlistmaker.domain.impl.sharing.SharingInteractorImpl
import com.google.gson.reflect.TypeToken
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {
    private const val SETTINGS_PREFS = "settings_prefs"
    private const val THEME_KEY = "theme_key"

    private const val SEARCH_HISTORY_PREFS = "search_history_prefs"
    private const val SEARCH_HISTORY_KEY = "search_history_key"

    private fun getTrackRetrofitService(): SearchAPI {
        val iTunesBaseUrl = "https://itunes.apple.com"
        val retrofit = Retrofit.Builder()
            .baseUrl(iTunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(SearchAPI::class.java)
    }

    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(getTrackRetrofitService(), context))
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            PrefsStorageClient(
                context,
                SEARCH_HISTORY_PREFS,
                SEARCH_HISTORY_KEY,
                object : TypeToken<MutableList<TrackDto>>() {}.type
            )
        )
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }

    private fun getThemeRepository(context: Context): ThemeRepository {
        return ThemeRepositoryImpl(
            PrefsStorageClient(
                context,
                SETTINGS_PREFS,
                THEME_KEY,
                object : TypeToken<Boolean>() {}.type
            ), context
        )
    }

    fun provideThemeInteractor(context: Context): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository(context))
    }

    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator(context))
    }

    private fun getMainNavigator(context: Context): MainNavigator {
        return MainNavigatorImpl(context)
    }

    fun provideMainInteractor(context: Context): MainInteractor {
        return MainInteractorImpl(getMainNavigator(context))
    }
}
