package com.example.playlistmaker.presentation.SearchActivity

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.search.models.SearchActivityState
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.util.Resource

class SearchActivityViewModel(context: Context) : ViewModel() {
    companion object {
        private const val INPUT_TEXT = "INPUT_TEXT"
        private const val INPUT_TEXT_DEF = ""
        private const val SEARCH_DEBOUNCE_DELAY = 1500L
        private const val HISTORY_UPDATE_DELAY = 600L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                SearchActivityViewModel(app)
            }
        }
    }

    private val searchHistoryInteractor = Creator.provideSearchHistoryInteractor(context)
    private val tracksInteractor = Creator.provideTracksInteractor(context)

    private val handler = Handler(Looper.getMainLooper())

    private var inputText: String = INPUT_TEXT_DEF
    private var lastQuery: String = ""

    private val stateLiveData = MutableLiveData<SearchActivityState>()
    fun observeState(): LiveData<SearchActivityState> = stateLiveData

    fun getSearchHistory() {
        handler.post {
            val lastTracksList = searchHistoryInteractor.getLastTracksList()
            renderState(
                SearchActivityState.SearchHistory(lastTracksList)
            )
        }
    }

    fun debounceSearch(query: String) {
        if (lastQuery == query) {
            return
        }
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
                SearchActivityState.Loading
            )
            tracksInteractor.searchTracks(query, object : TracksInteractor.TracksConsumer {
                override fun consume(resource: Resource<MutableList<Track>>) {
                    handler.post {
                        when (resource) {
                            is Resource.Success -> {
                                if (resource.data.isEmpty()) {
                                    renderState(
                                        SearchActivityState.Empty
                                    )
                                } else renderState(
                                    SearchActivityState.FoundTracks(resource.data)
                                )
                            }

                            is Resource.Error -> {
                                renderState(
                                    SearchActivityState.Error
                                )
                            }
                        }
                    }
                }
            })
        }
    }

    private fun renderState(state: SearchActivityState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }
}
