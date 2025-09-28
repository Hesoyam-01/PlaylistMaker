package com.example.playlistmaker.data.search.dto

import com.example.playlistmaker.domain.models.Track

data class TracksSearchResponse (val query: String, val results: List<TrackDto>) : Response()