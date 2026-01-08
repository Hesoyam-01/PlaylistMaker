package com.example.playlistmaker.presentation.playlistscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.domain.api.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.model.library.Playlist
import com.example.playlistmaker.domain.model.search.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlaylistScreenViewModel(
    private val playlistInteractor: PlaylistInteractor
) :
    ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistScreenState>()
    fun observePlaylistScreenState(): LiveData<PlaylistScreenState> = stateLiveData

    fun fillData(playlistId: Int) {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylistById(playlistId)
                .collect {
                    val tracks = playlistInteractor.getTracksByIds(it.trackIdList)
                    val totalTime =
                        fromMillisToMinutes(tracks.sumOf { track -> parseTime(track.trackTime) })
                    processResult(it, tracks, totalTime)
                }
        }
    }

    private fun processResult(playlist: Playlist, trackList: List<Track>, totalTime: Int) {
        renderState(
            PlaylistScreenState.Content(
                playlist.playlistName,
                playlist.playlistDescription,
                playlist.coverFilePath,
                playlist.trackCount,
                trackList,
                totalTime
            )
        )
        viewModelScope.launch {
            delay(MINIMAL_DELAY)
            if (trackList.isEmpty()) renderState(PlaylistScreenState.Empty)
        }
    }

    fun deleteTrackFromPlaylist(playlistId: Int, trackId: Int) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(playlistId, trackId)
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

    private fun fromMillisToMinutes(millis: Long): Int {
        return (millis / 60000).toInt()
    }

    private fun renderState(state: PlaylistScreenState) {
        stateLiveData.postValue(state)
    }

    private companion object {
        private const val MINIMAL_DELAY = 10L
    }
}