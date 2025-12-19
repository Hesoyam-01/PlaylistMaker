package com.example.playlistmaker.data.impl.search

import com.example.playlistmaker.data.client.NetworkClient
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.api.search.SearchRepository
import com.example.playlistmaker.domain.models.search.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Locale


class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
) : SearchRepository {
    private val dateFormat by lazy { SimpleDateFormat("m:ss", Locale.getDefault()) }

    override fun searchTracks(query: String): Flow<Resource<MutableList<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(query))

        when (response.resultCode) {
            200 -> {
                emit(Resource.Success((response as TracksSearchResponse).results.map {
                        Track(
                        trackId = it.trackId,
                        trackName = it.trackName,
                        artistName = it.artistName,
                        trackTime = dateFormat.format(it.trackTimeMillis),
                        artworkUrl100 = it.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"),
                        collectionName = it.collectionName,
                        releaseDate = it.releaseDate?.takeIf { it.length >= 4 }?.substring(0, 4),
                        primaryGenreName = it.primaryGenreName,
                        country = it.country,
                        previewUrl = it.previewUrl
                    )
                }.toMutableList()))
            }

            400 -> emit(Resource.Error())
            else -> emit(Resource.Error())
        }
    }
}