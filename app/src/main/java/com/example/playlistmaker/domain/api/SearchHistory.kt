package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistory {
    fun saveLastTrackList()
    fun addToLastTrackList(track: Track)
    fun loadLastTrackList()
}