package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl (private val repository: SearchHistoryRepository) : SearchHistoryInteractor {
    override fun getLastTracksList() : MutableList<Track> {
        return repository.getLastTracksList()
    }

    override fun saveLastTracksList() {
        repository.putLastTracksDtoListIntoSharedPrefs()
    }

    override fun addToLastTracksList(track: Track) {
        repository.addToLastTracksDtoList(track)
    }

    override fun loadLastTracksList() {
        repository.loadLastTracksListFromSharedPrefs()
    }
}