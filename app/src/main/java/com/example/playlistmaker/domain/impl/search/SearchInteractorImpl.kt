package com.example.playlistmaker.domain.impl.search

import com.example.playlistmaker.domain.api.search.SearchInteractor
import com.example.playlistmaker.domain.api.search.SearchRepository
import java.util.concurrent.Executors

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(query: String, consumer: SearchInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(query))
        }
    }

}