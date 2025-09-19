package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    val lastTracksList: MutableList<Track>
    fun saveLastTracksList()
    fun addToLastTracksList(track: Track)
    fun loadLastTracksList()
}