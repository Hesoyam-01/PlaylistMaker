package com.example.playlistmaker.presentation.player

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.player.MediaRepository
import com.example.playlistmaker.domain.models.player.MediaState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(previewUrl: String, private val mediaRepository: MediaRepository) : ViewModel() {
    companion object {
        const val ELAPSED_TIME_UPDATE_DELAY = 100L
    }
    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observePlayerState(): LiveData<PlayerState> = stateLiveData

    private var mediaState = MediaState.DEFAULT

    init {
        mediaRepository.observeMediaState().observeForever {
            mediaState = it
            if (mediaState == MediaState.PREPARED) {
                stateLiveData.postValue(PlayerState(false, dateFormat.format(0)))
                handler.removeCallbacks(progressTrackRunnable)
            }
        }
        mediaRepository.prepare(previewUrl)
    }

    private val handler = Handler(Looper.getMainLooper())

    private val dateFormat by lazy { SimpleDateFormat("m:ss", Locale.getDefault()) }


    private val progressTrackRunnable = object : Runnable {
        override fun run() {
            if (mediaState == MediaState.PLAYING) {
                stateLiveData.postValue(PlayerState(true, dateFormat.format(mediaRepository.getCurrentPosition())))
                handler.postDelayed(this, ELAPSED_TIME_UPDATE_DELAY)
            }
        }
    }

    fun playbackControl() {
        when (mediaState) {
            MediaState.PLAYING -> {
                mediaRepository.pause()
                stateLiveData.postValue(PlayerState(false, dateFormat.format(mediaRepository.getCurrentPosition())))
                handler.removeCallbacks(progressTrackRunnable)
            }

            MediaState.PREPARED, MediaState.PAUSED -> {
                mediaRepository.play()
                stateLiveData.postValue(PlayerState(true, dateFormat.format(mediaRepository.getCurrentPosition())))
                handler.postDelayed(
                    progressTrackRunnable,
                    ELAPSED_TIME_UPDATE_DELAY
                )
            } else -> {}
        }
    }

    fun onPause() {
        mediaRepository.pause()
        stateLiveData.postValue(PlayerState(false, dateFormat.format(mediaRepository.getCurrentPosition())))
    }

    override fun onCleared() {
        super.onCleared()
        mediaRepository.release()
    }
}