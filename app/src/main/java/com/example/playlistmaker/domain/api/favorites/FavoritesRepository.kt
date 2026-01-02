package com.example.playlistmaker.domain.api.favorites

import com.example.playlistmaker.domain.model.search.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    fun getFavoriteTracks() : Flow<List<Track>>

    suspend fun addToFavoriteTracks(track: Track)

    suspend fun deleteFromFavoriteTracks(track: Track)

    suspend fun getFavoriteTrackIds() : List<Int>

}