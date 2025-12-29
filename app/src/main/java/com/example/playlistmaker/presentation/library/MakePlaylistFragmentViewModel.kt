package com.example.playlistmaker.presentation.library

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.makeplaylist.MakePlaylistInteractor

class MakePlaylistFragmentViewModel(
    private val makePlaylistInteractor: MakePlaylistInteractor
) : ViewModel() {




    fun saveImageToPrivateStorage(uri: Uri) {
        makePlaylistInteractor.saveImageToPrivateStorage(uri)
    }
}