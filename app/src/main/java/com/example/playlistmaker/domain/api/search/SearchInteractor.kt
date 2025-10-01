package com.example.playlistmaker.domain.api.search

import com.example.playlistmaker.domain.models.search.Track
import com.example.playlistmaker.util.Resource

interface SearchInteractor {
    fun searchTracks(query: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(resource: Resource<MutableList<Track>>)
    }
}