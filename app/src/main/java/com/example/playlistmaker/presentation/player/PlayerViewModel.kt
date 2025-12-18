package com.example.playlistmaker.presentation.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.api.player.MediaInteractor
import com.example.playlistmaker.domain.models.player.MediaState
import com.example.playlistmaker.domain.models.search.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    previewUrl: String,
    trackId: Int,
    private val mediaInteractor: MediaInteractor,
    private val favoritesInteractor: FavoritesInteractor
) :
    ViewModel() {

    private val stateLiveData = MutableLiveData<PlayerState>()

    private var timerJob: Job? = null

    fun observePlayerState(): LiveData<PlayerState> = stateLiveData

    private var mediaState = MediaState.DEFAULT

    private val mediaStateObserver = Observer<MediaState> {
        mediaState = it
        if (mediaState == MediaState.PREPARED) {
            stateLiveData.postValue(
                PlayerState.Media(
                    mediaState,
                    dateFormat.format(mediaInteractor.getCurrentPosition())
                )
            )
        }
    }

    private val dateFormat by lazy { SimpleDateFormat("m:ss", Locale.getDefault()) }

    init {
        mediaInteractor.observeMediaState().observeForever(mediaStateObserver)
        mediaInteractor.prepare(previewUrl)
        viewModelScope.launch {
            val favoriteTrackIds = favoritesInteractor.favoriteTrackIds()
            val isFavorite = favoriteTrackIds.contains(trackId)
            stateLiveData.postValue(PlayerState.IsFavorite(isFavorite))
        }
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
        stateLiveData.postValue(
            PlayerState.Media(
                mediaState,
                dateFormat.format(mediaInteractor.getCurrentPosition())
            )
        )
        startTimer()
    }

    private fun pausePlayer() {
        mediaInteractor.pause()
        timerJob?.cancel()
        stateLiveData.postValue(
            PlayerState.Media(
                mediaState,
                dateFormat.format(mediaInteractor.getCurrentPosition())
            )
        )
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (isActive) {
                if (mediaState == MediaState.PLAYING) {
                    stateLiveData.postValue(
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
        stateLiveData.postValue(
            PlayerState.Media(
                mediaState,
                dateFormat.format(mediaInteractor.getCurrentPosition())
            )
        )
    }

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {
            if (track.isFavorite) {
                favoritesInteractor.deleteFromFavoriteTracks(track)
                stateLiveData.postValue((PlayerState.IsFavorite(false)))
            } else {
                favoritesInteractor.addToFavoriteTracks(track)
                stateLiveData.postValue((PlayerState.IsFavorite(true)))
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaInteractor.observeMediaState().removeObserver(mediaStateObserver)
        mediaInteractor.release()
    }
}