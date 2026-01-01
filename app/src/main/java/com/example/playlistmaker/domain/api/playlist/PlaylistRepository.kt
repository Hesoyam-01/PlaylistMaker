package com.example.playlistmaker.domain.api.playlist

import android.net.Uri
import com.example.playlistmaker.domain.model.library.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun makePlaylist(name: String, description: String?, coverFilePath: String?)

    suspend fun getPlaylists() : Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(playlistId: Int, newTrackId: Int)

    suspend fun saveImageAndGetPath(uri: Uri) : String
}