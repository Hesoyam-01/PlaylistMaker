package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun getLastTracksList() : MutableList<Track>
    fun putLastTracksListIntoSharedPrefs()
    fun addToLastTracksList(trackDto: Track)
    fun loadLastTracksListFromSharedPrefs()
}