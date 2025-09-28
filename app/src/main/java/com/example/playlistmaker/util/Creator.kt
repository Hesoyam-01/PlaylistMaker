package com.example.playlistmaker.util

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.impl.search.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.impl.settings.ThemeRepositoryImpl
import com.example.playlistmaker.data.impl.search.TracksRepositoryImpl
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.SearchAPI
import com.example.playlistmaker.data.storage.PrefsStorageClient
import com.example.playlistmaker.domain.api.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.search.SearchHistoryRepository
import com.example.playlistmaker.domain.api.settings.ThemeInteractor
import com.example.playlistmaker.domain.api.settings.ThemeRepository
import com.example.playlistmaker.domain.api.search.TracksInteractor
import com.example.playlistmaker.domain.api.search.TracksRepository
import com.example.playlistmaker.domain.impl.search.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.settings.ThemeInteractorImpl
import com.example.playlistmaker.domain.impl.search.TracksInteractorImpl
import com.google.gson.reflect.TypeToken
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {

    private const val SETTINGS_SHARED_PREFS = "settings_shared_prefs"

    private fun getTrackRetrofitService() : SearchAPI {
        val iTunesBaseUrl = "https://itunes.apple.com"
        val retrofit = Retrofit.Builder()
            .baseUrl(iTunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(SearchAPI::class.java)
    }

    private fun getTracksRepository(context: Context) : TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(getTrackRetrofitService(), context))
    }

    fun provideTracksInteractor(context: Context) : TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    private fun getSearchHistoryRepository(context: Context) : SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            PrefsStorageClient(
            context,
            "search_history_key",
            object : TypeToken<MutableList<TrackDto>>() {}.type)
        )
    }

    fun provideSearchHistoryInteractor(context: Context) : SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }

    private fun getSettingsSharedPrefs(context: Context) : SharedPreferences {
        return context.getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
    }

    private fun getThemeRepository(context: Context) : ThemeRepository {
        return ThemeRepositoryImpl(getSettingsSharedPrefs(context), context)
    }

    fun provideThemeInteractor(context: Context) : ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository(context))
    }
}

