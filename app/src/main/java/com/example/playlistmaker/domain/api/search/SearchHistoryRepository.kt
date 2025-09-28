package com.example.playlistmaker.domain.api.search

import com.example.playlistmaker.domain.models.search.Track
import com.example.playlistmaker.util.Resource

interface SearchHistoryRepository {
    fun getSearchHistory() : Resource<MutableList<Track>>
    fun putLastTracksDtoListIntoSharedPrefs()
    fun addToLastTracksDtoList(track: Track)
    fun loadLastTracksListDtoFromSharedPrefs()
    fun clearLastTracksDtoList()
}