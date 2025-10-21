package com.example.playlistmaker.di

import com.example.playlistmaker.presentation.library.FavoritesFragmentViewModel
import com.example.playlistmaker.presentation.library.PlaylistsFragmentViewModel
import com.example.playlistmaker.presentation.main.MainViewModel
import com.example.playlistmaker.presentation.player.PlayerViewModel
import com.example.playlistmaker.presentation.search.SearchViewModel
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        MainViewModel(get())
    }

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel { params ->
        PlayerViewModel(params.get(), get())
    }

    viewModel { params ->
        FavoritesFragmentViewModel(params.get())
    }

    viewModel { params ->
        PlaylistsFragmentViewModel(params.get())
    }
}