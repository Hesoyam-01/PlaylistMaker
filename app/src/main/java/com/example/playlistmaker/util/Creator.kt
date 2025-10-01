package com.example.playlistmaker.util

import android.content.Context
import com.example.playlistmaker.data.impl.main.MainNavigatorImpl
import com.example.playlistmaker.data.impl.search.TracksRepositoryImpl
import com.example.playlistmaker.data.impl.sharing.ExternalNavigatorImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.SearchAPI
import com.example.playlistmaker.domain.api.main.MainInteractor
import com.example.playlistmaker.domain.api.main.MainNavigator
import com.example.playlistmaker.domain.api.search.TracksInteractor
import com.example.playlistmaker.domain.api.search.TracksRepository
import com.example.playlistmaker.domain.api.sharing.ExternalNavigator
import com.example.playlistmaker.domain.api.sharing.SharingInteractor
import com.example.playlistmaker.domain.impl.main.MainInteractorImpl
import com.example.playlistmaker.domain.impl.search.TracksInteractorImpl
import com.example.playlistmaker.domain.impl.sharing.SharingInteractorImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {
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
