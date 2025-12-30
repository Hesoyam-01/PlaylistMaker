package com.example.playlistmaker.presentation.library

import com.example.playlistmaker.domain.model.library.Playlist

sealed interface PlaylistsState {

    data object Empty : PlaylistsState

    data class Content(val playlists: List<Playlist>) : PlaylistsState
}