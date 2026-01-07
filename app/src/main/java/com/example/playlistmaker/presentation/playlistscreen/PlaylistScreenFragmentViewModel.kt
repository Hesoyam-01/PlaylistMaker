package com.example.playlistmaker.presentation.playlistscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.model.search.Track
import com.example.playlistmaker.presentation.player.PlayerState
import kotlinx.coroutines.launch

class PlaylistScreenFragmentViewModel(
    private val playlistInteractor: PlaylistInteractor
) :
    ViewModel() {

    private val stateLiveData = MutableLiveData<List<Track>>()
    fun observePlaylistScreenState(): LiveData<List<Track>> = stateLiveData

    fun getTracksByIds(ids: List<Int>) {
        viewModelScope.launch {
            playlistInteractor
                .getTracksByIds(ids)
                .collect {
                    processResult(it)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        renderState(tracks)
    }

    private fun renderState(tracks: List<Track>) {
        stateLiveData.postValue(tracks)
    }

}