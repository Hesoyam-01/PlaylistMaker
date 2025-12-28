package com.example.playlistmaker.domain.api.makeplaylist

import android.net.Uri
import com.example.playlistmaker.domain.models.library.Playlist

interface MakePlaylistInteractor {
    suspend fun addToPlaylists(playlist: Playlist)

    fun saveImageToPrivateStorage(uri: Uri)
}