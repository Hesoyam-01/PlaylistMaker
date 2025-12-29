package com.example.playlistmaker.data.db.converters

import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.model.search.Track

class TrackDbConverter {

    fun map(track: Track) : TrackEntity {
        return TrackEntity(
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

    fun map(track: TrackEntity) : Track {
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