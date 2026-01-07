package com.example.playlistmaker.data.impl.search

import com.example.playlistmaker.data.client.NetworkClient
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.api.search.SearchRepository
import com.example.playlistmaker.domain.model.search.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Locale


class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
) : SearchRepository {

    override fun searchTracks(query: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(query))

        when (response.resultCode) {
            200 -> {
                emit(Resource.Success((response as TracksSearchResponse).results.map {
                        Track(
                        trackId = it.trackId,
                        trackName = it.trackName,
                        artistName = it.artistName,
                        trackTime = formatMillisToHMSOptionalHours(it.trackTimeMillis),
                        artworkUrl100 = it.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"),
                        collectionName = it.collectionName,
                        releaseDate = it.releaseDate?.takeIf { it.length >= 4 }?.substring(0, 4),
                        primaryGenreName = it.primaryGenreName,
                        country = it.country,
                        previewUrl = it.previewUrl
                    )
                }))
            }

            400 -> emit(Resource.Error())
            else -> emit(Resource.Error())
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