package com.example.playlistmaker.domain.api.playlist

import android.net.Uri

interface PlaylistInteractor {
    suspend fun makePlaylist(name: String, description: String?, coverFilePath: String?)

    suspend fun saveImageAndGetPath(uri: Uri) : String
}