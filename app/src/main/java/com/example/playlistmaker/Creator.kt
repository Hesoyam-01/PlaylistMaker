package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.SearchAPI
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {
    private const val TRACK_SHARED_PREFS = "track_shared_prefs"

    private fun getTracksRepository() : TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(getTrackRetrofitService()))
    }

    fun getTracksInteractor() : TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }


    fun getTrackSharedPrefs(context: Context) : SharedPreferences {
        return context.getSharedPreferences(TRACK_SHARED_PREFS, Context.MODE_PRIVATE)
    }

    fun getSearchHistoryRepository(context: Context) : SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(getTrackSharedPrefs(context))
    }

    private fun getTrackRetrofitService() : SearchAPI {
        val iTunesBaseUrl = "https://itunes.apple.com"
        val retrofit = Retrofit.Builder()
            .baseUrl(iTunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(SearchAPI::class.java)
    }
}

