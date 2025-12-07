package com.example.playlistmaker.domain.api.search

import com.example.playlistmaker.domain.models.search.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchTracks(query: String) : Flow<Resource<MutableList<Track>>>
}