package com.example.playlistmaker.data

import com.example.playlistmaker.domain.models.Track

sealed class SearchResult {
    data class Success(val tracks: MutableList<Track>) : SearchResult()
    data class Failure(val errorCode: Int) : SearchResult()
}