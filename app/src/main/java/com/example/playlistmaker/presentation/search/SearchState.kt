package com.example.playlistmaker.presentation.search

import com.example.playlistmaker.domain.model.search.Track

sealed interface SearchState {
    data object Loading : SearchState

    data class FoundTracks(
        val tracksList: List<Track>
    ) : SearchState

    data class SearchHistory(
        val lastTracksList: List<Track>
    ) : SearchState

    data object Error : SearchState

    data object Empty : SearchState
}
