package com.example.playlistmaker.presentation.player

import com.example.playlistmaker.domain.models.player.MediaState

data class PlayerState (val mediaState: MediaState, val elapsedTime: String)