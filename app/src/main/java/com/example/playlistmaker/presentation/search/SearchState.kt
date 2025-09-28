package com.example.playlistmaker.presentation.search

import com.example.playlistmaker.domain.models.Track

sealed interface SearchState {
    data object Loading : SearchState

    data class FoundTracks(
        val tracksList: MutableList<Track>
    ) : SearchState

    data object SearchHistory : SearchState

    data object Error : SearchState

    data object Empty : SearchState

}
