package com.example.playlistmaker.presentation.playlistscreen

import com.example.playlistmaker.domain.model.search.Track

sealed interface PlaylistScreenState {

    data class Content(
        val playlistName: String,
        val playlistDescription: String?,
        val coverFilePath: String?,
        val trackCount: Int,
        val trackList: List<Track>,
        val totalTime: Int
    ) : PlaylistScreenState

    data object EmptyPlaylist : PlaylistScreenState

    data class DeleteDialog(
        val playlistName: String
    ) : PlaylistScreenState

    data object NothingToSend : PlaylistScreenState
}