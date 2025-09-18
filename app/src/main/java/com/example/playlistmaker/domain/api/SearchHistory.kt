package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.search.TrackAdapter

interface SearchHistory {
    val lastTracksList: MutableList<Track>
    fun saveLastTrackList()
    fun addToLastTrackList(track: Track)
    fun loadLastTrackList()
}