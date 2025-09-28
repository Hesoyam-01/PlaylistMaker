package com.example.playlistmaker.util

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.ThemeRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.SearchAPI
import com.example.playlistmaker.data.storage.PrefsStorageClient
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import com.google.gson.reflect.TypeToken
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {

    private const val TRACK_SHARED_PREFS = "track_shared_prefs"
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

