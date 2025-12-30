package com.example.playlistmaker.presentation.library

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.playlist.PlaylistInteractor
import kotlinx.coroutines.launch

class MakePlaylistFragmentViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val savedImagePathLiveData = MutableLiveData<String>()
    fun observeImagePath(): LiveData<String> = savedImagePathLiveData

    fun makePlaylist(title: String, description: String?, coverFilePath: String?) {
        viewModelScope.launch {
            playlistInteractor.makePlaylist(title, description, coverFilePath)
        }
    }

    fun saveImageAndGetPath(uri: Uri) {
        viewModelScope.launch {
            savedImagePathLiveData.postValue(playlistInteractor.saveImageAndGetPath(uri))
        }
    }
}