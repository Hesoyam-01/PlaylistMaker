package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun getLastTracksList() : MutableList<Track>
    fun saveLastTracksList()
    fun addToLastTracksList(track: Track)
    fun clearLastTracksList()
}