package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.data.client.NetworkClient
import com.example.playlistmaker.data.client.StorageClient
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.SearchAPI
import com.example.playlistmaker.data.storage.PrefsStorageClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<SearchAPI> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SearchAPI::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences("local_storage", Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<StorageClient<MutableList<TrackDto>>>(named("searchHistoryStorage")) {
        PrefsStorageClient(
            get(),
            get(),
            "search_history_key",
            object : TypeToken<MutableList<TrackDto>>() {}.type
        )
    }

    single<StorageClient<Boolean>>(named("themeStorage")) {
        PrefsStorageClient(get(),
            get(),
            "theme_key",
            object : TypeToken<Boolean>() {}.type)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

}