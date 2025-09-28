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
        const val MEDIA_STATE_DEFAULT = 0
        const val MEDIA_STATE_PREPARED = 1
        const val MEDIA_STATE_PLAYING = 2
        const val MEDIA_STATE_PAUSED = 3
        const val ELAPSED_TIME_UPDATE_DELAY = 100L

        fun getFactory(previewUrl: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(previewUrl)
            }
        }
    }

    private val playerStateLiveData = MutableLiveData(MEDIA_STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData

    private val elapsedTimeLiveData = MutableLiveData("0:00")
    fun observeElapsedTime(): LiveData<String> = elapsedTimeLiveData

    private var mediaPlayer = MediaPlayer()

    init {
        preparePlayer()
    }

    private val handler = Handler(Looper.getMainLooper())

    private val dateFormat by lazy { SimpleDateFormat("m:ss", Locale.getDefault()) }

    private val updateElapsedTimeRunnable = object : Runnable {
        override fun run() {
            if (playerStateLiveData.value == MEDIA_STATE_PLAYING) {
                elapsedTimeLiveData.postValue(dateFormat
                    .format(mediaPlayer.currentPosition))
                handler.postDelayed(this, ELAPSED_TIME_UPDATE_DELAY)
            }
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(MEDIA_STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            playerStateLiveData.postValue(MEDIA_STATE_PREPARED)
            elapsedTimeLiveData.value = dateFormat.format(0)
            handler.removeCallbacks(updateElapsedTimeRunnable)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(MEDIA_STATE_PLAYING)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerStateLiveData.postValue(MEDIA_STATE_PAUSED)
    }

    fun playbackControl() {
        when (playerStateLiveData.value) {
            MEDIA_STATE_PLAYING -> {
                pausePlayer()
                handler.removeCallbacks(updateElapsedTimeRunnable)
            }

            MEDIA_STATE_PREPARED, MEDIA_STATE_PAUSED -> {
                startPlayer()
                handler.postDelayed(
                    updateElapsedTimeRunnable,
                    ELAPSED_TIME_UPDATE_DELAY
                )
            }
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