package com.example.playlistmaker.presentation.library

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.playlist.PlaylistInteractor
import kotlinx.coroutines.launch

class MakePlaylistFragmentViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    fun makePlaylist(title: String, description: String?, coverFilePath: String?) {
        viewModelScope.launch {
            playlistInteractor.makePlaylist(title, description, coverFilePath)
        }
    }

    fun saveImageAndGetPath(uri: Uri): String {
        viewModelScope.launch {
            return playlistInteractor.saveImageAndGetPath(uri)
        }
    }
}