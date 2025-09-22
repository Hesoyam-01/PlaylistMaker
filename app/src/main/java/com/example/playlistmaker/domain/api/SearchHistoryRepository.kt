package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun getLastTracksList() : MutableList<Track>
    fun putLastTracksDtoListIntoSharedPrefs()
    fun addToLastTracksDtoList(track: Track)
    fun loadLastTracksListDtoFromSharedPrefs()
    fun clearLastTracksDtoList()
}