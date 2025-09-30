package com.example.playlistmaker.presentation.search

import com.example.playlistmaker.domain.models.search.Track

sealed interface SearchState {
    data object Loading : SearchState

    data class FoundTracks(
        val tracksList: MutableList<Track>
    ) : SearchState

    data class SearchHistory(
        val lastTracksList: MutableList<Track>
    ) : SearchState

    data object Error : SearchState

    data object Empty : SearchState
}
