package com.example.playlistmaker.domain.model.library

data class Playlist (
    val playlistId: Int = 0,
    val playlistName: String,
    val playlistDescription: String?,
    val coverFilePath: String?,
    val trackIdList: List<Int> = emptyList(),
    val trackCount: Int = 0
)
