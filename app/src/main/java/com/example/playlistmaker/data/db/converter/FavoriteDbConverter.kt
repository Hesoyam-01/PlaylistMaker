package com.example.playlistmaker.data.db.converter

import com.example.playlistmaker.data.db.entity.FavoriteEntity
import com.example.playlistmaker.domain.model.search.Track

class FavoriteDbConverter {

    fun map(track: Track) : FavoriteEntity {
        return FavoriteEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            System.currentTimeMillis()
        )
    }

    fun map(track: FavoriteEntity) : Track {
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }
}