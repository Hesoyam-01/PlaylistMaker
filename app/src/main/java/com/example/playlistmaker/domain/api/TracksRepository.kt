package com.example.playlistmaker.domain.api

import com.example.playlistmaker.data.SearchResult
import com.example.playlistmaker.domain.models.Track

interface TracksRepository {
    fun searchTracks(query: String) : SearchResult
}