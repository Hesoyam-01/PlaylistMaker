package com.example.playlistmaker.domain.api.player

import androidx.lifecycle.LiveData
import com.example.playlistmaker.domain.models.player.MediaState

interface MediaRepository {
    fun prepare(previewUrl: String)
    fun play()
    fun pause()
    fun getCurrentPosition(): Int
    fun release()
    fun observeMediaState() : LiveData<MediaState>
}