package com.example.playlistmaker.domain.api.playlist

import android.net.Uri
import com.example.playlistmaker.domain.model.library.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun makePlaylist(name: String, description: String?, coverFilePath: String?)

    suspend fun getPlaylists() : Flow<List<Playlist>>

    suspend fun saveImageAndGetPath(uri: Uri) : String
}