package com.example.playlistmaker.presentation.player

import com.example.playlistmaker.domain.model.library.Playlist
import com.example.playlistmaker.domain.model.player.MediaState

sealed interface PlayerState {
    data class BottomSheet(
        val bottomSheetState: Int
    ) : PlayerState

    data class TrackInPlaylistStatus(
        val isPresent: Boolean,
        val playlistName: String
    ) : PlayerState

    data class Playlists(
        val list: List<Playlist>
    ) : PlayerState

    data class Media(
        val mediaState: MediaState,
        val elapsedTime: String
    ) : PlayerState

    data class IsFavorite(
        val isFavorite: Boolean
    ) : PlayerState
}