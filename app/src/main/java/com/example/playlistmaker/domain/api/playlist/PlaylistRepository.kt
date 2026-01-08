package com.example.playlistmaker.domain.api.playlist

import android.net.Uri
import com.example.playlistmaker.domain.model.library.Playlist
import com.example.playlistmaker.domain.model.search.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun makePlaylist(name: String, description: String?, coverFilePath: String?)

    fun getPlaylists(): Flow<List<Playlist>>

    fun getPlaylistById(playlistId: Int): Flow<Playlist>

    suspend fun addTrackToPlaylistById(playlistId: Int, newTrackId: Int)

    suspend fun saveImageAndGetPath(uri: Uri): String

    suspend fun saveTrackFromPlaylist(track: Track)

    suspend fun getTracksByIds(ids: List<Int>): List<Track>

    suspend fun deleteTrackFromPlaylistById(playlistId: Int, trackId: Int)

    suspend fun deletePlaylistById(playlistId: Int)
}