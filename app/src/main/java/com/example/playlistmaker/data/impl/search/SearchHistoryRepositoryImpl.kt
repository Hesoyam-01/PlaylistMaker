package com.example.playlistmaker.data.impl.search

import com.example.playlistmaker.data.client.StorageClient
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.api.search.SearchHistoryRepository
import com.example.playlistmaker.domain.model.search.Track
import com.example.playlistmaker.util.Resource
import java.util.Locale

class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<MutableList<TrackDto>>,
) : SearchHistoryRepository {
    private companion object {
        const val MAX_TRACK_HISTORY = 10
    }

    private val lastTracksDtoList = mutableListOf<TrackDto>()

    init {
        loadLastTracksDtoList()
    }

    override fun saveLastTracksDtoList() {
        storage.storeData(lastTracksDtoList)
        loadLastTracksDtoList()
    }

    override fun addToLastTracksDtoList(track: Track) {
        val trackDto = fromTrackToTrackDto(track)
        lastTracksDtoList.removeAll { it.trackId == trackDto.trackId }
        if (lastTracksDtoList.size >= MAX_TRACK_HISTORY) {
            lastTracksDtoList.removeAt(9)
        }
        lastTracksDtoList.add(0, trackDto)
        saveLastTracksDtoList()
    }

    override fun loadLastTracksDtoList() {
        lastTracksDtoList.clear()
        storage.getData()?.let { lastTracksDtoList.addAll(it) }
    }

    override fun clearLastTracksDtoList() {
        lastTracksDtoList.clear()
        saveLastTracksDtoList()
    }

    override fun getSearchHistory(): Resource<List<Track>> {
        val lastTracksList = lastTracksDtoList.map {
            val track = fromTrackDtoToTrack(it)
            track
        }
        return Resource.Success(lastTracksList)
    }

    private fun fromTrackDtoToTrack(trackDto: TrackDto): Track {
        val track = Track(
                trackId = trackDto.trackId,
                trackName = trackDto.trackName,
                artistName = trackDto.artistName,
                trackTime = formatMillisToHMSOptionalHours(trackDto.trackTimeMillis),
                artworkUrl100 = trackDto.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"),
                collectionName = trackDto.collectionName,
                releaseDate = trackDto.releaseDate?.takeIf { it.length >= 4 }?.substring(0, 4),
                primaryGenreName = trackDto.primaryGenreName,
                country = trackDto.country,
                previewUrl = trackDto.previewUrl
            )
        return track
    }

    private fun fromTrackToTrackDto(track: Track) : TrackDto {
        val trackDto = TrackDto(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = parseTime(track.trackTime),
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
        return trackDto
    }

    private fun parseTime(timeStr: String): Long {
        val parts = timeStr.split(":")
        return when (parts.size) {
            2 -> {
                val minutes = parts[0].toLongOrNull() ?: 0L
                val seconds = parts[1].toLongOrNull() ?: 0L
                minutes * 60_000 + seconds * 1_000
            }
            3 -> {
                val hours = parts[0].toLongOrNull() ?: 0L
                val minutes = parts[1].toLongOrNull() ?: 0L
                val seconds = parts[2].toLongOrNull() ?: 0L
                hours * 3_600_000 + minutes * 60_000 + seconds * 1_000
            }
            else -> 0L
        }
    }

    private fun formatMillisToHMSOptionalHours(milliseconds: Long): String {
        val hours = milliseconds / 3600000
        val minutes = (milliseconds % 3600000) / 60000
        val seconds = (milliseconds % 60000) / 1000

        return if (hours > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
        }
    }

}
