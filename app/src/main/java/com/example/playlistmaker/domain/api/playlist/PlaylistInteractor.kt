package com.example.playlistmaker.domain.api.playlist

import android.net.Uri
import com.example.playlistmaker.domain.model.library.Playlist
import com.example.playlistmaker.domain.model.search.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun makePlaylist(name: String, description: String?, coverFilePath: String?)

    fun getPlaylists() : Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(playlistId: Int, newTrackId: Int)

    suspend fun saveImageAndGetPath(uri: Uri) : String

    suspend fun saveTrackFromPlaylist(track: Track)

    fun getTracksByIds(ids: List<Int>): Flow<List<Track>>
}