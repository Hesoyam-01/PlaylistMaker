package com.example.playlistmaker.data.impl.favorites

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.converter.FavoriteDbConverter
import com.example.playlistmaker.data.db.entity.FavoriteEntity
import com.example.playlistmaker.domain.api.favorites.FavoritesRepository
import com.example.playlistmaker.domain.model.search.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val favoriteDbConverter: FavoriteDbConverter
) : FavoritesRepository {

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.favoriteDao().getTracks()
        emit(convertFromFavoriteEntity(tracks))
    }

    override suspend fun addToFavoriteTracks(track: Track) {
        appDatabase.favoriteDao().insertTrack(favoriteDbConverter.map(track))
    }

    override suspend fun deleteFromFavoriteTracks(track: Track) {
        appDatabase.favoriteDao().deleteTrack(favoriteDbConverter.map(track))
    }

    override suspend fun getFavoriteTrackIds() : List<Int> {
        return appDatabase.favoriteDao().getTrackIds()
    }

    private fun convertFromFavoriteEntity(tracks: List<FavoriteEntity>) : List<Track> {
        return tracks.map { favoriteDbConverter.map(it) }
    }
}