package com.example.playlistmaker.domain.impl.makeplaylist

import android.net.Uri
import com.example.playlistmaker.domain.api.makeplaylist.MakePlaylistInteractor
import com.example.playlistmaker.domain.api.makeplaylist.MakePlaylistRepository

class MakePlaylistInteractorImpl(private val repository: MakePlaylistRepository) : MakePlaylistInteractor {

    override suspend fun makePlaylist(name: String, description: String?, coverFilePath: String?) {
        repository.makePlaylist(name, description, coverFilePath)
    }

    override fun saveImageToPrivateStorage(uri: Uri) : String {
        return repository.saveImageToPrivateStorage(uri)
    }
}