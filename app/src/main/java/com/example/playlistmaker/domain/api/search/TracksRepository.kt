package com.example.playlistmaker.domain.api.search

import com.example.playlistmaker.domain.models.search.Track
import com.example.playlistmaker.util.Resource

interface TracksRepository {
    fun searchTracks(query: String) : Resource<MutableList<Track>>
}