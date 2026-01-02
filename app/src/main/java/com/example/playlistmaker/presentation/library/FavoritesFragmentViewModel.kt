package com.example.playlistmaker.presentation.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.model.search.Track
import kotlinx.coroutines.launch

class FavoritesFragmentViewModel(private val favoritesInteractor: FavoritesInteractor) :
    ViewModel() {

    private val favoritesLiveData = MutableLiveData<FavoritesState>()
    fun observeFavoritesLiveData(): LiveData<FavoritesState> = favoritesLiveData

    fun fillData() {
        viewModelScope.launch {
            favoritesInteractor
                .getFavoriteTracks()
                .collect {
                    processResult(it)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoritesState.Empty)
        } else {
            renderState(FavoritesState.Content(tracks))
        }
    }

    private fun renderState(state: FavoritesState) {
        favoritesLiveData.postValue(state)
    }
}