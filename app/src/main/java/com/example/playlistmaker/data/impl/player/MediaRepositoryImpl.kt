package com.example.playlistmaker.data.impl.player

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.domain.api.player.MediaRepository
import com.example.playlistmaker.domain.model.player.MediaState

class MediaRepositoryImpl(private val mediaPlayer: MediaPlayer) : MediaRepository {
    private val mediaStateLiveData = MutableLiveData<MediaState>()
    override fun getMediaStateLiveData(): LiveData<MediaState> = mediaStateLiveData

    private var isReadyForSeek = false

    override fun prepare(previewUrl: String) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.setOnPreparedListener {
            mediaStateLiveData.postValue(MediaState.PREPARED)
        }
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnCompletionListener {
            mediaStateLiveData.postValue(MediaState.PREPARED)
            if (isReadyForSeek) {
                mediaPlayer.seekTo(0)
                isReadyForSeek = false
            }
        }
    }

    override fun play() {
        isReadyForSeek = true
        mediaPlayer.start()
        mediaStateLiveData.postValue(MediaState.PLAYING)
    }

    override fun pause() {
        mediaPlayer.pause()
        mediaStateLiveData.postValue(MediaState.PAUSED)
    }


    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun release() {
        mediaPlayer.release()
    }
}