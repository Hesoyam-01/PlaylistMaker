package com.example.playlistmaker.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.search.SearchInteractor
import com.example.playlistmaker.domain.model.search.Track
import com.example.playlistmaker.util.Resource
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val searchInteractor: SearchInteractor
) : ViewModel() {

    private var lastQuery: String = ""

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeSearchState(): LiveData<SearchState> = stateLiveData

    fun addToSearchHistory(track: Track) {
        searchHistoryInteractor.addToSearchHistory(track)
        (searchHistoryInteractor.getSearchHistory() as? Resource.Success)?.data
            ?: listOf()
    }

    fun clearSearchHistory() {
        searchHistoryInteractor.clearSearchHistory()
        renderState(
            SearchState.SearchHistory(listOf())
        )
    }

    fun getSearchHistory() {
        val lastTracksList =
            (searchHistoryInteractor.getSearchHistory() as? Resource.Success)?.data
                ?: listOf()
        renderState(
            SearchState.SearchHistory(lastTracksList)
        )
    }

    val debounceSearch = debounce<String>(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        true
    ) { query ->
        lastQuery = query
        searchRequest(query)
    }

    fun cancelSearchRequest() {
        viewModelScope.coroutineContext.cancelChildren()
    }

    private fun searchRequest(query: String) {
        if (query.isNotEmpty()) {
            renderState(
                SearchState.Loading
            )

            viewModelScope.launch {
                searchInteractor
                    .searchTracks(query)
                    .collect { resource ->
                        processResult(resource)
                    }
            }
        }
    }

    private fun processResult(resource: Resource<List<Track>>) {
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

    fun repeatLastSearch() {
        debounceSearch(lastQuery)
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 1500L
    }
}
