package com.example.playlistmaker.presentation.playlistscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.playlist.PlaylistInteractor
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistScreenFragmentViewModel(
    private val playlistInteractor: PlaylistInteractor
) :
    ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistScreenState>()
    fun observePlaylistScreenState(): LiveData<PlaylistScreenState> = stateLiveData

    private val dateFormatFromMssToMillis by lazy {
        fun(durationStr: String): Long {
            val date = SimpleDateFormat("m:ss", Locale.getDefault()).parse(durationStr)
            return date?.time ?: 0
        }
    }

    private val dateFormatFromMillisToMinutes by lazy {
        SimpleDateFormat("m", Locale.getDefault())
    }

    fun getTracksByIdsAndTotalTime(ids: List<Int>) {
        viewModelScope.launch {
            val tracks = playlistInteractor.getTracksByIds(ids)
            val totalTime = dateFormatFromMillisToMinutes.format(tracks.sumOf { dateFormatFromMssToMillis(it.trackTime) })
            renderState(PlaylistScreenState.Content(tracks, totalTime))
        }
    }

    private fun renderState(state: PlaylistScreenState) {
        stateLiveData.postValue(state)
    }

}