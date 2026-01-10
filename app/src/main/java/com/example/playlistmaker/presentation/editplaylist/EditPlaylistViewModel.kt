package com.example.playlistmaker.presentation.editplaylist

import com.example.playlistmaker.domain.api.playlist.PlaylistInteractor
import com.example.playlistmaker.presentation.makeplaylist.MakePlaylistViewModel

class EditPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : MakePlaylistViewModel(playlistInteractor) {

    override fun makePlaylist(title: String, description: String?, coverFilePath: String?) {

    }
}