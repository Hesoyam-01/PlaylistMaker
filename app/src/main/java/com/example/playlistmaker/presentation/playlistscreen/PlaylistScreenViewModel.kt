package com.example.playlistmaker.presentation.playlistscreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.api.sharing.SharingInteractor
import com.example.playlistmaker.domain.model.library.Playlist
import com.example.playlistmaker.domain.model.search.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlaylistScreenViewModel(
    application: Application,
    private val playlistInteractor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor
) :
    AndroidViewModel(application) {
    private val context = getApplication<Application>()

    private lateinit var playlist: Playlist
    private lateinit var trackList: List<Track>

    private val stateLiveData = MutableLiveData<PlaylistScreenState>()
    fun observePlaylistScreenState(): LiveData<PlaylistScreenState> = stateLiveData

    fun fillData(playlistId: Int) {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylistById(playlistId)
                .collect {
                    playlist = it
                    trackList = playlistInteractor.getTracksByIds(it.trackIdList)
                    val totalTime =
                        fromMillisToMinutes(trackList.sumOf { track -> parseTime(track.trackTime) })
                    processResult(it, trackList, totalTime)
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
            if (trackList.isEmpty()) renderState(PlaylistScreenState.EmptyPlaylist)
        }
    }

    fun deleteTrackFromPlaylistById(trackId: Int) {
        viewModelScope.launch {
            playlistInteractor.deleteTracksFromPlaylistByIds(playlist.playlistId, listOf(trackId))
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistInteractor.deleteTracksFromPlaylistByIds(
                playlist.playlistId,
                playlist.trackIdList
            )
            playlistInteractor.deletePlaylistById(playlist.playlistId)
        }
    }

    fun deletePlaylistDialog() {
        renderState(PlaylistScreenState.DeleteDialog(playlist.playlistName))
    }

    fun sharePlaylist() {
        sharingInteractor.sharePlaylist(sharePlaylistMessage())
    }

    private fun sharePlaylistMessage(): String {
        val stringBuilder = StringBuilder()

        stringBuilder.appendLine(playlist.playlistName)
        stringBuilder.appendLine(playlist.playlistDescription)
        stringBuilder.appendLine(
            context.resources.getQuantityString(
                R.plurals.track_count,
                playlist.trackCount, playlist.trackCount
            )
        )
        trackList.withIndex().forEach { (index, track) ->
            stringBuilder.appendLine("${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTime})")
        }
        return stringBuilder.toString()
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