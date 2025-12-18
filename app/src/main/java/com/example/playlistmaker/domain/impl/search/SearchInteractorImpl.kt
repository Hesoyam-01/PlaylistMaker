package com.example.playlistmaker.domain.impl.search

import com.example.playlistmaker.domain.api.search.SearchInteractor
import com.example.playlistmaker.domain.api.search.SearchRepository
import com.example.playlistmaker.domain.models.search.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {

    override fun searchTracks(query: String) : Flow<Resource<MutableList<Track>>> {
        return repository.searchTracks(query)
    }

}