package com.example.playlistmaker.presentation.search

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.search.SearchInteractor
import com.example.playlistmaker.domain.models.search.Track
import com.example.playlistmaker.util.Resource

class SearchViewModel(
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val searchInteractor: SearchInteractor
) : ViewModel() {
    private val handler = Handler(Looper.getMainLooper())

    private var lastQuery: String = ""

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeSearchState(): LiveData<SearchState> = stateLiveData

    fun addToSearchHistory(track: Track) {
        searchHistoryInteractor.addToSearchHistory(track)
        (searchHistoryInteractor.getSearchHistory() as? Resource.Success)?.data
            ?: mutableListOf()
    }

    fun clearSearchHistory() {
        searchHistoryInteractor.clearSearchHistory()
        renderState(
            SearchState.SearchHistory(mutableListOf())
        )
    }

    fun getSearchHistory() {
        val lastTracksList =
            (searchHistoryInteractor.getSearchHistory() as? Resource.Success)?.data
                ?: mutableListOf()
        renderState(
            SearchState.SearchHistory(lastTracksList)
        )
    }

    fun debounceSearch(query: String) {
        this.lastQuery = query
        val searchRunnable = Runnable { searchRequest(query) }
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    private fun searchRequest(query: String) {
        if (query.isNotEmpty()) {
            renderState(
                SearchState.Loading
            )
            searchInteractor.searchTracks(query, object : SearchInteractor.TracksConsumer {
                override fun consume(resource: Resource<MutableList<Track>>) {
                    handler.post {
                        when (resource) {
                            is Resource.Success -> {
                                if (resource.data.isEmpty()) {
                                    renderState(
                                        SearchState.Empty
                                    )
                                } else renderState(
                                    SearchState.FoundTracks(resource.data)
                                )
                            }

                            is Resource.Error -> {
                                renderState(
                                    SearchState.Error
                                )
                            }
                        }
                    }
                }
            })
        }
    }

    fun repeatLastSearch() {
        debounceSearch(lastQuery)
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 1500L
        private val SEARCH_REQUEST_TOKEN = Any()
    }
}
