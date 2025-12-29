package com.example.playlistmaker.domain.api.favorites

import com.example.playlistmaker.domain.model.search.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    fun favoriteTracks() : Flow<MutableList<Track>>

    suspend fun addToFavoriteTracks(track: Track)

    suspend fun deleteFromFavoriteTracks(track: Track)

    suspend fun favoriteTrackIds() : List<Int>

}