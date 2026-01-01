package com.example.playlistmaker.domain.impl.playlist

import android.net.Uri
import com.example.playlistmaker.domain.api.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.api.playlist.PlaylistRepository
import com.example.playlistmaker.domain.model.library.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val repository: PlaylistRepository) : PlaylistInteractor {

    override suspend fun makePlaylist(name: String, description: String?, coverFilePath: String?) {
        repository.makePlaylist(name, description, coverFilePath)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun addTrackToPlaylist(playlistId: Int, newTrackId: Int) {
        repository.addTrackToPlaylist(playlistId, newTrackId)
    }

    override suspend fun saveImageAndGetPath(uri: Uri) : String {
        return repository.saveImageAndGetPath(uri)
    }
}