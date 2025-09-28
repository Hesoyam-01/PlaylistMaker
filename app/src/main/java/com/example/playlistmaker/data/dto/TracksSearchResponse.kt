package com.example.playlistmaker.data.dto

data class TracksSearchResponse (val query: String, val results: List<TrackDto>) : Response()