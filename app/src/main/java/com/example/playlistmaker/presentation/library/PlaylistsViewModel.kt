package com.example.playlistmaker.presentation.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.model.library.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val playlistLiveData = MutableLiveData<PlaylistsState>()
    fun observePlaylistLiveData(): LiveData<PlaylistsState> = playlistLiveData


    fun fillData() {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect {
                    processResult(it)
                }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlaylistsState.Empty)
        } else {
            renderState(PlaylistsState.Content(playlists))
        }
    }

    private fun renderState(state: PlaylistsState) {
        playlistLiveData.postValue(state)
    }
}