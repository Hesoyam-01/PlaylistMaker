package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.data.client.NetworkClient
import com.example.playlistmaker.data.client.StorageClient
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.impl.main.MainNavigatorImpl
import com.example.playlistmaker.data.impl.sharing.ExternalNavigatorImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.SearchAPI
import com.example.playlistmaker.data.storage.PrefsStorageClient
import com.example.playlistmaker.domain.api.main.MainNavigator
import com.example.playlistmaker.domain.api.sharing.ExternalNavigator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object StorageKeys {
    const val PREFS_NAME = "local_storage"
    const val SEARCH_HISTORY_NAME = "searchHistoryStorage"
    const val SEARCH_HISTORY_KEY = "search_history_key"
    const val THEME_NAME = "themeStorage"
    const val THEME_KEY = "theme_key"
}

val dataModule = module {

    single<MainNavigator> {
        MainNavigatorImpl(androidContext())
    }

    single<SearchAPI> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SearchAPI::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

    factory {
        MediaPlayer()
    }

    factory { Gson() }

    single {
        androidContext()
            .getSharedPreferences(StorageKeys.PREFS_NAME, Context.MODE_PRIVATE)
    }

    single<StorageClient<MutableList<TrackDto>>>(named(StorageKeys.SEARCH_HISTORY_NAME)) {
        PrefsStorageClient(
            get(),
            get(),
            StorageKeys.SEARCH_HISTORY_KEY,
            object : TypeToken<MutableList<TrackDto>>() {}.type
        )
    }

    single<StorageClient<Boolean>>(named(StorageKeys.THEME_NAME)) {
        PrefsStorageClient(
            get(),
            get(),
            StorageKeys.THEME_KEY,
            object : TypeToken<Boolean>() {}.type)
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

}