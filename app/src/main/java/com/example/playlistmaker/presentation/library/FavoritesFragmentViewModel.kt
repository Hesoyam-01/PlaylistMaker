package com.example.playlistmaker.presentation.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoritesFragmentViewModel(isFavoritesEmpty: Boolean) : ViewModel() {
    private val isFavoritesEmptyLiveData = MutableLiveData(isFavoritesEmpty)
    fun observeIsFavoritesEmpty(): LiveData<Boolean> = isFavoritesEmptyLiveData
}