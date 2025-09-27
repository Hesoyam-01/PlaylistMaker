package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Resource

interface TracksInteractor {
    fun searchTracks(query: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(resource: Resource<MutableList<Track>>)
    }
}