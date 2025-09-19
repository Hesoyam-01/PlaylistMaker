package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun getLastTracksList(consumer: SearchHistoryConsumer)
    fun saveLastTracksList()
    fun addToLastTracksList(track: Track)
    fun loadLastTracksList()

    interface SearchHistoryConsumer {
        fun consume(lastTracksList: MutableList<Track>)
    }
}