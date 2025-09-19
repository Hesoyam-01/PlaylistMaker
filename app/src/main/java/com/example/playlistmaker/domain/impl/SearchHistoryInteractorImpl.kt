package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl (private val repository: SearchHistoryRepository) : SearchHistoryInteractor {
    override fun getLastTracksList(consumer: SearchHistoryInteractor.SearchHistoryConsumer) {
        consumer.consume(repository.getLastTracksList())
    }

    override fun saveLastTracksList() {
        repository.putLastTracksListIntoSharedPrefs()
    }

    override fun addToLastTracksList(track: Track) {
        repository.addToLastTracksList(track)
    }

    override fun loadLastTracksList() {
        repository.loadLastTracksListFromSharedPrefs()
    }
}