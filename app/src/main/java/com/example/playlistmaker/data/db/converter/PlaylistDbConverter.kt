package com.example.playlistmaker.data.db.converter

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
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            coverFilePath = playlist.coverFilePath,
            trackIdList = gson.fromJson(playlist.trackIdList, object : TypeToken<List<Int>>() {}.type) ?: emptyList(),
            trackCount = playlist.trackCount
        )
    }

    fun mapIdList(idList: List<Int>) : String {
        return gson.toJson(idList)
    }

    fun mapIdList(idList: String?) : List<Int> {
        return gson.fromJson(idList, object : TypeToken<List<Int>>() {}.type) ?: emptyList()
    }
}