package com.example.playlistmaker.presentation.player

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.player.MediaInteractor
import com.example.playlistmaker.domain.models.player.MediaState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(previewUrl: String, private val mediaInteractor: MediaInteractor) : ViewModel() {
    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observePlayerState(): LiveData<PlayerState> = stateLiveData

    private var mediaState = MediaState.DEFAULT
    private val mediaStateObserver = Observer<MediaState> {
        mediaState = it
        if (mediaState == MediaState.PREPARED) {
            stateLiveData.postValue(PlayerState(false, dateFormat.format(mediaInteractor.getCurrentPosition())))
            handler.removeCallbacks(progressTrackRunnable)
        }
    }

    private val handler = Handler(Looper.getMainLooper())

    private val dateFormat by lazy { SimpleDateFormat("m:ss", Locale.getDefault()) }

    init {
        mediaInteractor.observeMediaState().observeForever(mediaStateObserver)
        mediaInteractor.prepare(previewUrl)
    }

    private val progressTrackRunnable = object : Runnable {
        override fun run() {
            if (mediaState == MediaState.PLAYING) {
                stateLiveData.postValue(PlayerState(true, dateFormat.format(mediaInteractor.getCurrentPosition())))
                handler.postDelayed(this, ELAPSED_TIME_UPDATE_DELAY)
            }
        }
    }

    fun playbackControl() {
        when (mediaState) {
            MediaState.PLAYING -> {
                mediaInteractor.pause()
                stateLiveData.postValue(PlayerState(false, dateFormat.format(mediaInteractor.getCurrentPosition())))
                handler.removeCallbacks(progressTrackRunnable)
            }

            MediaState.PREPARED, MediaState.PAUSED -> {
                mediaInteractor.play()
                stateLiveData.postValue(PlayerState(true, dateFormat.format(mediaInteractor.getCurrentPosition())))
                handler.postDelayed(
                    progressTrackRunnable,
                    ELAPSED_TIME_UPDATE_DELAY
                )
            } else -> {}
        }
    }

    fun onPause() {
        mediaInteractor.pause()
        stateLiveData.postValue(PlayerState(false, dateFormat.format(mediaInteractor.getCurrentPosition())))
    }

    override fun onCleared() {
        super.onCleared()
        mediaInteractor.observeMediaState().removeObserver(mediaStateObserver)
        mediaInteractor.release()
    }

    companion object {
        const val ELAPSED_TIME_UPDATE_DELAY = 100L
    }
}