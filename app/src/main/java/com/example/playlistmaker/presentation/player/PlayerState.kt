package com.example.playlistmaker.presentation.player

import com.example.playlistmaker.domain.models.player.MediaState

sealed interface PlayerState {
    data class Media(
        val mediaState: MediaState,
        val elapsedTime: String
    ) : PlayerState

    data class IsFavorite(
        val isFavorite: Boolean
    ) : PlayerState
}