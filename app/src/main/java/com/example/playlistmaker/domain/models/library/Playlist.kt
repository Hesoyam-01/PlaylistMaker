package com.example.playlistmaker.domain.models.library

data class Playlist (
    val playlistName: String,
    val playlistDescription: String,
    val coverFilePath: String,
    val trackIdList: List<Int>,
    val trackCount: Int
)
