package com.example.playlistmaker.domain.api

interface TracksInteractor {
    fun searchTracks()

    interface TracksConsumer {
        fun consume()
    }
}