package com.example.playlistmaker.domain.impl.search

import com.example.playlistmaker.domain.api.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.search.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Resource

class SearchHistoryInteractorImpl (private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {
    override fun getSearchHistory() : Resource<MutableList<Track>> {
        return repository.getSearchHistory()
    }

    override fun addToSearchHistory(track: Track) {
        repository.addToLastTracksDtoList(track)
    }

    override fun clearSearchHistory() {
        repository.clearLastTracksDtoList()
    }
}