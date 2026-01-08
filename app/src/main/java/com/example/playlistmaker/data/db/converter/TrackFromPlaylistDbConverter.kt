package com.example.playlistmaker.data.db.converter

import com.example.playlistmaker.data.db.entity.TrackFromPlaylistEntity
import com.example.playlistmaker.domain.model.search.Track

class TrackFromPlaylistDbConverter {

    fun map(track: Track) : TrackFromPlaylistEntity {
        return TrackFromPlaylistEntity(
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

    fun map(track: TrackFromPlaylistEntity) : Track {
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