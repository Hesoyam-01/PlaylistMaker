package com.example.playlistmaker.presentation.player

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val previewUrl: String) : ViewModel() {
    companion object {
        const val ELAPSED_TIME_UPDATE_DELAY = 100L

        fun getFactory(previewUrl: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(previewUrl)
            }
        }
    }
    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observePlayerState(): LiveData<PlayerState> = stateLiveData

    private var mediaPlayer = MediaPlayer()
    private var mediaState = MediaState.DEFAULT

    init {
        preparePlayer()
    }

    private val handler = Handler(Looper.getMainLooper())

    private val dateFormat by lazy { SimpleDateFormat("m:ss", Locale.getDefault()) }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            mediaState = MediaState.PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            mediaState = MediaState.PREPARED
            stateLiveData.postValue(PlayerState(false, dateFormat.format(0)))
            handler.removeCallbacks(progressTrackRunnable)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        mediaState = MediaState.PLAYING
        stateLiveData.postValue(PlayerState(true, dateFormat.format(0)))
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        mediaState = MediaState.PAUSED
        stateLiveData.postValue(PlayerState(false, dateFormat.format(mediaPlayer.currentPosition)))
    }

    private val progressTrackRunnable = object : Runnable {
        override fun run() {
            if (mediaState == MediaState.PLAYING) {
                stateLiveData.postValue(PlayerState(true, dateFormat.format(mediaPlayer.currentPosition)))
                handler.postDelayed(this, ELAPSED_TIME_UPDATE_DELAY)
            }
        }
    }

    fun playbackControl() {
        when (mediaState) {
            MediaState.PLAYING -> {
                pausePlayer()
                handler.removeCallbacks(progressTrackRunnable)
            }

            MediaState.PREPARED, MediaState.PAUSED -> {
                startPlayer()
                handler.postDelayed(
                    progressTrackRunnable,
                    ELAPSED_TIME_UPDATE_DELAY
                )
            } else -> {}
        }
    }

    fun onPause() {
        pausePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }
}