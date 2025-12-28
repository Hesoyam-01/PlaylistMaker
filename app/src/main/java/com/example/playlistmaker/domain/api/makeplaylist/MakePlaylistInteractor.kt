package com.example.playlistmaker.domain.api.makeplaylist

import android.net.Uri

interface MakePlaylistInteractor {
    fun saveImageToPrivateStorage(uri: Uri)
}