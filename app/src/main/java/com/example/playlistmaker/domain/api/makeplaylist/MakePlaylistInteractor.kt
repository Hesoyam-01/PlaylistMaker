package com.example.playlistmaker.domain.api.makeplaylist

import android.net.Uri

interface MakePlaylistInteractor {
    suspend fun makePlaylist(name: String, description: String?, coverFilePath: String?)

    fun saveImageToPrivateStorage(uri: Uri)
}