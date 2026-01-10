package com.example.playlistmaker.di

import com.example.playlistmaker.presentation.editplaylist.EditPlaylistViewModel
import com.example.playlistmaker.presentation.library.FavoritesViewModel
import com.example.playlistmaker.presentation.library.PlaylistsViewModel
import com.example.playlistmaker.presentation.main.MainViewModel
import com.example.playlistmaker.presentation.makeplaylist.MakePlaylistViewModel
import com.example.playlistmaker.presentation.player.PlayerViewModel
import com.example.playlistmaker.presentation.playlistscreen.PlaylistScreenViewModel
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
        PlayerViewModel(params.get(), get(), get(), get())
    }

    viewModel {
        FavoritesViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        MakePlaylistViewModel(get())
    }

    viewModel {
        PlaylistScreenViewModel(get(), get(), get())
    }

    viewModel {
        EditPlaylistViewModel(get())
    }

}