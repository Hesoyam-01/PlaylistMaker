package com.example.playlistmaker.ui.search.models

import com.example.playlistmaker.domain.models.Track

sealed interface SearchActivityState {
    data object Loading : SearchActivityState

    data class FoundTracks(
        val tracksList: MutableList<Track>
    ) : SearchActivityState

    data class SearchHistory(
        val lastTracksList: MutableList<Track>
    ) : SearchActivityState

    data object Error : SearchActivityState

    data object Empty : SearchActivityState

}
