package com.example.playlistmaker.data.db.converters

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.model.library.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConverter(private val gson: Gson) {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            coverFilePath = playlist.coverFilePath,
            trackIdList = gson.toJson(playlist.trackIdList),
            trackCount = playlist.trackCount
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.coverFilePath,
            gson.fromJson(playlist.trackIdList, object : TypeToken<Int>() {}.type),
            playlist.trackCount
        )
    }
}