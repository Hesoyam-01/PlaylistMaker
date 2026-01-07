package com.example.playlistmaker.presentation.playlistscreen

import com.example.playlistmaker.domain.model.search.Track

sealed interface PlaylistScreenState {
    data class Content(
        val tracks: List<Track>,
        val totalTime: String
    ) : PlaylistScreenState
}