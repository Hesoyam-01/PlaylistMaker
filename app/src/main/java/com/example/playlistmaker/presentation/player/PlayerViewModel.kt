package com.example.playlistmaker.presentation.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.api.player.MediaInteractor
import com.example.playlistmaker.domain.api.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.model.library.Playlist
import com.example.playlistmaker.domain.model.player.MediaState
import com.example.playlistmaker.domain.model.search.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val mediaInteractor: MediaInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val favoritesInteractor: FavoritesInteractor
) :
    ViewModel() {

    private var timerJob: Job? = null

    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observePlayerState(): LiveData<PlayerState> = stateLiveData

    private var bottomSheetState = BottomSheetBehavior.STATE_HIDDEN
    private var isBottomSheetStateSaved = false

    private var mediaState = MediaState.DEFAULT

    private val mediaStateObserver = Observer<MediaState> {
        mediaState = it

        renderState(
            PlayerState.Media(
                mediaState,
                dateFormat.format(mediaInteractor.getCurrentPosition())
            )
        )

    }

    private val dateFormat by lazy { SimpleDateFormat("m:ss", Locale.getDefault()) }

    init {
        mediaInteractor.observeMediaState().observeForever(mediaStateObserver)
        mediaInteractor.prepare(track.previewUrl)
    }

    fun playbackControl() {
        when (mediaState) {
            MediaState.PLAYING -> {
                pausePlayer()
            }

            MediaState.PREPARED, MediaState.PAUSED -> {
                startPlayer()
            }

            else -> {}
        }
    }

    private fun startPlayer() {
        mediaInteractor.play()
        startTimer()
    }

    private fun pausePlayer() {
        mediaInteractor.pause()
        timerJob?.cancel()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (isActive) {
                if (mediaState == MediaState.PLAYING) {
                    renderState(
                        PlayerState.Media(
                            mediaState,
                            dateFormat.format(mediaInteractor.getCurrentPosition())
                        )
                    )
                }
                delay(300L)
            }
        }
    }

    fun onPause() {
        mediaInteractor.pause()
        renderState(
            PlayerState.Media(
                mediaState,
                dateFormat.format(mediaInteractor.getCurrentPosition())
            )
        )
    }

    fun onFavoriteClicked(isFavorite: Boolean) {
        viewModelScope.launch {
            if (isFavorite) {
                favoritesInteractor.deleteFromFavoriteTracks(track)
                renderState(PlayerState.IsFavorite(false))
            } else {
                favoritesInteractor.addToFavoriteTracks(track)
                renderState(PlayerState.IsFavorite(true))
            }
        }
    }

    fun fillData() {
        viewModelScope.launch {
            val favoriteTrackIds = favoritesInteractor.getFavoriteTrackIds()
            val isFavorite = favoriteTrackIds.contains(track.trackId)
            renderState(PlayerState.IsFavorite(isFavorite))
            playlistInteractor
                .getPlaylists()
                .collect {
                    processResult(it)
                }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        renderState(PlayerState.Playlists(playlists))
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        if (playlist.trackIdList.contains(track.trackId)) renderState(
            PlayerState.TrackInPlaylistStatus(
                true,
                playlist.playlistName
            )
        )
        else {
            renderState(
                PlayerState.TrackInPlaylistStatus(
                    false,
                    playlist.playlistName
                )
            )
            viewModelScope.launch {
                playlistInteractor.addTrackToPlaylist(playlist.playlistId, track.trackId)
                playlistInteractor.saveTrackFromPlaylist(track)
            }
        }
    }

    fun getBottomSheetState() {
        if (isBottomSheetStateSaved) renderState(PlayerState.BottomSheet(bottomSheetState))
        isBottomSheetStateSaved = false
    }

    fun saveBottomSheetState(bottomSheetState: Int) {
        this.bottomSheetState = bottomSheetState
        isBottomSheetStateSaved = true
    }

    private fun renderState(state: PlayerState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        mediaInteractor.observeMediaState().removeObserver(mediaStateObserver)
        mediaInteractor.release()
    }
}