package com.example.playlistmaker.data.impl.player

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.domain.api.player.MediaRepository
import com.example.playlistmaker.domain.models.player.MediaState

class MediaRepositoryImpl(private val mediaPlayer: MediaPlayer) : MediaRepository {

    private val mediaStateLiveData = MutableLiveData<MediaState>()
    override fun getMediaStateLiveData(): LiveData<MediaState> = mediaStateLiveData

    override fun prepare(previewUrl: String) {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.setOnPreparedListener {
            Log.d("end", "end")
            mediaStateLiveData.postValue(MediaState.PREPARED)
        }
        Log.d("start", "start")
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnCompletionListener {
            mediaStateLiveData.postValue(MediaState.PREPARED)
        }
    }

    override fun play() {
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