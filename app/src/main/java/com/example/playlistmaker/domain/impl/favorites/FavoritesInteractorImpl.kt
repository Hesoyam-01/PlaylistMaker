package com.example.playlistmaker.domain.impl.favorites

import com.example.playlistmaker.domain.api.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.api.favorites.FavoritesRepository
import com.example.playlistmaker.domain.models.search.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val repository: FavoritesRepository
) : FavoritesInteractor {

    override fun favoriteTracks(): Flow<MutableList<Track>> {
        return repository.favoriteTracks()
    }

    override suspend fun addToFavoriteTracks(track: Track) {
        repository.addToFavoriteTracks(track)
    }

    override suspend fun deleteFromFavoriteTracks(track: Track) {
        repository.deleteFromFavoriteTracks(track)
    }

    override suspend fun favoriteTrackIds(): List<Int> {
        return repository.favoriteTrackIds()
    }

}