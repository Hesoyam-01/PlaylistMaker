package com.example.playlistmaker.data.impl.favorites

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.converters.TrackDbConverter
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.api.favorites.FavoritesRepository
import com.example.playlistmaker.domain.model.search.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter
) : FavoritesRepository {

    override fun favoriteTracks(): Flow<MutableList<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override suspend fun addToFavoriteTracks(track: Track) {
        appDatabase.trackDao().insertTrack(trackDbConverter.map(track))
    }

    override suspend fun deleteFromFavoriteTracks(track: Track) {
        appDatabase.trackDao().deleteTrack(trackDbConverter.map(track))
    }

    override suspend fun favoriteTrackIds() : List<Int> {
        return appDatabase.trackDao().getTrackIds()
    }

    private fun convertFromTrackEntity(tracks: MutableList<TrackEntity>) : MutableList<Track> {
        return tracks.map { trackDbConverter.map(it) }.toMutableList()
    }
}