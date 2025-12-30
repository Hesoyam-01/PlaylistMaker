package com.example.playlistmaker.domain.impl.favorites

import com.example.playlistmaker.domain.api.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.api.favorites.FavoritesRepository
import com.example.playlistmaker.domain.model.search.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val repository: FavoritesRepository
) : FavoritesInteractor {

    override fun getFavoriteTracks(): Flow<MutableList<Track>> {
        return repository.getFavoriteTracks()
    }

    override suspend fun addToFavoriteTracks(track: Track) {
        repository.addToFavoriteTracks(track)
    }

    override suspend fun deleteFromFavoriteTracks(track: Track) {
        repository.deleteFromFavoriteTracks(track)
    }

    override suspend fun getFavoriteTrackIds(): List<Int> {
        return repository.getFavoriteTrackIds()
    }

}