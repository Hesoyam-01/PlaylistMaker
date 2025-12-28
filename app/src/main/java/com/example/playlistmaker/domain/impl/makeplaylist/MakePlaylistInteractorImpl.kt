package com.example.playlistmaker.domain.impl.makeplaylist

import android.net.Uri
import com.example.playlistmaker.domain.api.makeplaylist.MakePlaylistInteractor
import com.example.playlistmaker.domain.api.makeplaylist.MakePlaylistRepository
import com.example.playlistmaker.domain.models.library.Playlist

class MakePlaylistInteractorImpl(private val repository: MakePlaylistRepository) : MakePlaylistInteractor {

    override suspend fun addToPlaylists(playlist: Playlist) {
        repository.addToPlaylists(playlist)
    }

    override fun saveImageToPrivateStorage(uri: Uri) {
        repository.saveImageToPrivateStorage(uri)
    }
}