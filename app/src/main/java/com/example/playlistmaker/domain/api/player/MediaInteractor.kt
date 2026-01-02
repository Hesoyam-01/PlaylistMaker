package com.example.playlistmaker.domain.api.player

import androidx.lifecycle.LiveData
import com.example.playlistmaker.domain.model.player.MediaState

interface MediaInteractor {
    fun observeMediaState() : LiveData<MediaState>
    fun prepare(previewUrl: String)
    fun play()
    fun pause()
    fun getCurrentPosition(): Int
    fun release()
}