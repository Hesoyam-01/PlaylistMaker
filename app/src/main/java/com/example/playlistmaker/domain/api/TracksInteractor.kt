package com.example.playlistmaker.domain.api

import com.example.playlistmaker.data.SearchResult

interface TracksInteractor {
    fun searchTracks(query: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consumeSearchResult(searchResult: SearchResult)
    }
}