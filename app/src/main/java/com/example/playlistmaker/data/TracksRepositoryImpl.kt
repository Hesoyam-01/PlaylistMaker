package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    private val dateFormat by lazy { SimpleDateFormat("m:ss", Locale.getDefault()) }

    override fun searchTracks(query: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(query))

        return if (response.resultCode == 200) {
            (response as TracksSearchResponse).results.map {
                Track(
                    trackId = it.trackId,
                    trackName = it.trackName,
                    artistName = it.artistName,
                    trackTime = dateFormat.format(it.trackTimeMillis),
                    artworkUrl100 = it.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"),
                    collectionName = it.collectionName,
                    releaseDate = it.releaseDate?.takeIf { it.length >= 4 }?.substring(0, 4) ?: it.releaseDate,
                    primaryGenreName = it.primaryGenreName,
                    country = it.country,
                    previewUrl = it.previewUrl
                )
            }
        } else emptyList()
    }
}