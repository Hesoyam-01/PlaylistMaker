package com.example.playlistmaker.presentation.library

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.makeplaylist.MakePlaylistInteractor
import kotlinx.coroutines.launch

class MakePlaylistFragmentViewModel(
    private val makePlaylistInteractor: MakePlaylistInteractor
) : ViewModel() {

    fun makePlaylist(title: String, description: String?, coverFilePath: String?) {
        viewModelScope.launch {
            makePlaylistInteractor.makePlaylist(title, description, coverFilePath)
        }
    }

    fun saveImageToPrivateStorage(uri: Uri) : String {
        return makePlaylistInteractor.saveImageToPrivateStorage(uri)
    }
}