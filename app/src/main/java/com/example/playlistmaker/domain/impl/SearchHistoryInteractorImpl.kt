package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Resource

class SearchHistoryInteractorImpl (private val repository: SearchHistoryRepository) : SearchHistoryInteractor {
    override fun getSearchHistory() : Resource<MutableList<Track>> {
        return repository.getSearchHistory()
    }

    override fun saveSearchHistory() {
        repository.putLastTracksDtoListIntoSharedPrefs()
    }

    override fun addToSearchHistory(track: Track) {
        repository.addToLastTracksDtoList(track)
    }

    override fun clearSearchHistory() {
        repository.clearLastTracksDtoList()
    }
}