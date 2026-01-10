package com.example.playlistmaker.presentation.editplaylist

import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.playlist.PlaylistInteractor
import com.example.playlistmaker.presentation.makeplaylist.MakePlaylistViewModel
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : MakePlaylistViewModel(playlistInteractor) {

    fun updatePlaylist(
        playlistId: Int, playlistName: String, playlistDescription: String?, coverFilePath: String?
    ) {
        viewModelScope.launch {
            playlistInteractor.updatePlaylist(
                playlistId,
                playlistName,
                playlistDescription,
                coverFilePath
            )
        }
    }
}