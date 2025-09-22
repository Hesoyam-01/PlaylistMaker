package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.models.Track

data class TracksSearchResponse (val query: String, val results: List<TrackDto>) : Response()