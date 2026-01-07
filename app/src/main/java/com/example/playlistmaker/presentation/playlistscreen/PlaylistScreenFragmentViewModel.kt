package com.example.playlistmaker.presentation.playlistscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.playlist.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistScreenFragmentViewModel(
    private val playlistInteractor: PlaylistInteractor
) :
    ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistScreenState>()
    fun observePlaylistScreenState(): LiveData<PlaylistScreenState> = stateLiveData

    fun getTracksByIdsAndTotalTime(ids: List<Int>) {
        viewModelScope.launch {
            val tracks = playlistInteractor.getTracksByIds(ids)
            val totalTime =
                fromMillisToMinutes(tracks.sumOf { parseTime(it.trackTime) })

            renderState(PlaylistScreenState.Content(tracks, totalTime))
        }
    }

    private fun parseTime(timeStr: String): Long {
        val parts = timeStr.split(":")
        return when (parts.size) {
            2 -> {
                val minutes = parts[0].toLongOrNull() ?: 0L
                val seconds = parts[1].toLongOrNull() ?: 0L
                minutes * 60_000 + seconds * 1_000
            }
            3 -> {
                val hours = parts[0].toLongOrNull() ?: 0L
                val minutes = parts[1].toLongOrNull() ?: 0L
                val seconds = parts[2].toLongOrNull() ?: 0L
                hours * 3_600_000 + minutes * 60_000 + seconds * 1_000
            }
            else -> 0L
        }
    }

    private fun fromMillisToMinutes(millis: Long) : Int {
        return (millis / 60000).toInt()
    }

    private fun renderState(state: PlaylistScreenState) {
        stateLiveData.postValue(state)
    }

}