package com.example.playlistmaker.domain.api.makeplaylist

import android.net.Uri

interface MakePlaylistRepository {
    fun saveImageToPrivateStorage(uri: Uri)
}