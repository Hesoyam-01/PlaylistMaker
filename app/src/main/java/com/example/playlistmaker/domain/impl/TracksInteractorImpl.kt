package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(val repository: TracksRepository) : TracksInteractor {
    val executor = Executors.newCachedThreadPool()

    override fun searchTracks() {
        repository.searchTracks()
   }

}