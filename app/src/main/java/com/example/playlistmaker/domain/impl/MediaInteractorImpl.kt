package com.example.playlistmaker.domain.impl

import androidx.lifecycle.LiveData
import com.example.playlistmaker.domain.api.player.MediaInteractor
import com.example.playlistmaker.domain.api.player.MediaRepository
import com.example.playlistmaker.domain.models.player.MediaState

class MediaInteractorImpl(private val repository: MediaRepository) : MediaInteractor {

    override fun observeMediaState(): LiveData<MediaState> {
        return repository.getMediaStateLiveData()
    }

    override fun prepare(previewUrl: String) {
        repository.prepare(previewUrl)
    }

    override fun play() {
        repository.play()
    }

    override fun pause() {
        repository.pause()
    }

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }

    override fun release() {
        repository.release()
    }

}