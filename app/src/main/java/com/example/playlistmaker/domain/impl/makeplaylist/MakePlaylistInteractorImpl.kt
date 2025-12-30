package com.example.playlistmaker.domain.impl.makeplaylist

import android.net.Uri
import com.example.playlistmaker.domain.api.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.api.playlist.PlaylistRepository

class MakePlaylistInteractorImpl(private val repository: PlaylistRepository) : PlaylistInteractor {

    override suspend fun makePlaylist(name: String, description: String?, coverFilePath: String?) {
        repository.makePlaylist(name, description, coverFilePath)
    }

    override suspend fun saveImageAndGetPath(uri: Uri) : String {
        return repository.saveImageAndGetPath(uri)
    }
}