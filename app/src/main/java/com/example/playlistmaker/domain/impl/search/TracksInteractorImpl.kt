package com.example.playlistmaker.domain.impl.search

import com.example.playlistmaker.domain.api.search.TracksInteractor
import com.example.playlistmaker.domain.api.search.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(query: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(query))
        }
    }

}