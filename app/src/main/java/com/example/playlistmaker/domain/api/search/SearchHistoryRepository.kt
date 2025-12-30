package com.example.playlistmaker.domain.api.search

import com.example.playlistmaker.domain.model.search.Track
import com.example.playlistmaker.util.Resource

interface SearchHistoryRepository {
    fun getSearchHistory() : Resource<List<Track>>
    fun saveLastTracksDtoList()
    fun addToLastTracksDtoList(track: Track)
    fun loadLastTracksDtoList()
    fun clearLastTracksDtoList()
}