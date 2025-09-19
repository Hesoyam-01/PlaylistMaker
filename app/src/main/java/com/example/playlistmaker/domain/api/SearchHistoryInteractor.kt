package com.example.playlistmaker.domain.api

interface SearchHistoryInteractor {
    fun saveLastTracksList()
    fun addToLastTracksList()
    fun loadLastTracksList()

    interface Consumer {
        fun consume()
    }
}