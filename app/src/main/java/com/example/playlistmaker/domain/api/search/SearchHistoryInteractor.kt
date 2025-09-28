package com.example.playlistmaker.domain.api.search

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Resource

interface SearchHistoryInteractor {
    fun getSearchHistory() : Resource<MutableList<Track>>
    fun addToSearchHistory(track: Track)
    fun clearSearchHistory()
}