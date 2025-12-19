package com.example.playlistmaker.presentation.library

import com.example.playlistmaker.domain.models.search.Track

sealed interface FavoritesState {

    data object Empty : FavoritesState

    data class Content(val tracks: MutableList<Track>) : FavoritesState
}