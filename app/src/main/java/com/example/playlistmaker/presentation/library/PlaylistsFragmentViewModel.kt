package com.example.playlistmaker.presentation.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistsFragmentViewModel(isPlaylistsEmpty: Boolean) : ViewModel() {
    private val isPlaylistsEmptyLiveData = MutableLiveData(isPlaylistsEmpty)
    fun observeIsPlaylistsEmpty(): LiveData<Boolean> = isPlaylistsEmptyLiveData
}