package com.example.playlistmaker.presentation.library

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.makeplaylist.MakePlaylistInteractor
import kotlinx.coroutines.launch

class MakePlaylistFragmentViewModel(
    private val makePlaylistInteractor: MakePlaylistInteractor
) : ViewModel() {

    fun makePlaylist(title: String, description: String?, coverPath: String?) {
        viewModelScope.launch {
            makePlaylistInteractor.makePlaylist(title, description, coverPath)
        }
    }

    fun saveImageToPrivateStorage(uri: Uri) {
        makePlaylistInteractor.saveImageToPrivateStorage(uri)
    }
}