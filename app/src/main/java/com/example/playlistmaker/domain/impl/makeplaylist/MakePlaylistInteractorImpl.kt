package com.example.playlistmaker.domain.impl.makeplaylist

import android.net.Uri
import com.example.playlistmaker.domain.api.makeplaylist.MakePlaylistInteractor
import com.example.playlistmaker.domain.api.makeplaylist.MakePlaylistRepository

class MakePlaylistInteractorImpl(private val repository: MakePlaylistRepository) : MakePlaylistInteractor {
    override fun saveImageToPrivateStorage(uri: Uri) {
        repository.saveImageToPrivateStorage(uri)
    }
}