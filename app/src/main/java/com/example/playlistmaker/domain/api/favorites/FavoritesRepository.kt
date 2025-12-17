package com.example.playlistmaker.domain.api.favorites

import com.example.playlistmaker.domain.models.search.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    fun favoriteTracks() : Flow<List<Track>>

    suspend fun addToFavoriteTracks(track: Track)

    suspend fun deleteFromFavoriteTracks(track: Track)

}