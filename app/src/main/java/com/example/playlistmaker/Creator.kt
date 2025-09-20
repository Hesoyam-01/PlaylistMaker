package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.ThemeRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.SearchAPI
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.models.Track
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {

    private const val TRACK_SHARED_PREFS = "track_shared_prefs"
    private const val SETTINGS_SHARED_PREFS = "settings_shared_prefs"

    private fun getTracksRepository() : TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(getTrackRetrofitService()))
    }

    fun getTracksInteractor() : TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }


    private fun getTrackSharedPrefs(context: Context) : SharedPreferences {
        return context.getSharedPreferences(TRACK_SHARED_PREFS, Context.MODE_PRIVATE)
    }

    private fun getSearchHistoryRepository(context: Context) : SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(getTrackSharedPrefs(context))
    }

    fun getSearchHistoryInteractor(context: Context) : SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }

    private fun getTrackRetrofitService() : SearchAPI {
        val iTunesBaseUrl = "https://itunes.apple.com"
        val retrofit = Retrofit.Builder()
            .baseUrl(iTunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(SearchAPI::class.java)
    }

    private fun getSettingsSharedPrefs(context: Context) : SharedPreferences {
        return context.getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
    }

    fun getThemeRepository(context: Context) : ThemeRepository {
        return ThemeRepositoryImpl(getSettingsSharedPrefs(context))
    }

    fun getThemeInteractor(context: Context) : ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository(context), context)
    }
}

