package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.data.dto.LastTracksDto
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Locale

class SearchHistoryRepositoryImpl(
    private val trackSharedPrefs: SharedPreferences
) : SearchHistoryRepository {

    private val lastTracksDto = LastTracksDto(mutableListOf())
    private var lastTracks = mutableListOf<Track>()

    private val dateFormatFromMillisToMss by lazy { SimpleDateFormat("m:ss", Locale.getDefault()) }
    private val dateFormatFromMssToMillis by lazy { fun(durationStr: String): Long {
        val date = dateFormatFromMillisToMss.parse(durationStr)
        return date?.time ?: 0
    } }

    private val gson = Gson()

    private val trackSharedPrefsListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == LAST_TRACK_LIST_KEY) {
                loadLastTracksListFromSharedPrefs()
            }
        }

    init {
        loadLastTracksListFromSharedPrefs()
        trackSharedPrefs.registerOnSharedPreferenceChangeListener(trackSharedPrefsListener)
    }


    override fun putLastTracksListIntoSharedPrefs() {
        trackSharedPrefs.edit()
            .putString(LAST_TRACK_LIST_KEY, gson.toJson(lastTracksDto.list))
            .apply()
    }

    override fun addToLastTracksList(track: Track) {
        val trackDto = TrackDto(trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = dateFormatFromMssToMillis(track.trackTime),
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl)
        lastTracksDto.list.removeAll { it.trackId == trackDto.trackId }
        if (lastTracksDto.list.size >= MAX_TRACK_HISTORY) {
            lastTracksDto.list.removeAt(9)
        }
        lastTracksDto.list.add(0, trackDto)
        putLastTracksListIntoSharedPrefs()
    }

    override fun loadLastTracksListFromSharedPrefs() {
        val jsonLastTrackList = trackSharedPrefs.getString(LAST_TRACK_LIST_KEY, null)
        val type = object : TypeToken<MutableList<TrackDto>>() {}.type
        val loadedTracks = gson.fromJson<List<TrackDto>>(jsonLastTrackList, type) ?: emptyList()
        lastTracksDto.list.clear()
        lastTracksDto.list.addAll(loadedTracks)
    }

    override fun getLastTracksList(): MutableList<Track> {
        lastTracks = lastTracksDto.list.map {
            Track(
                trackId = it.trackId,
                trackName = it.trackName,
                artistName = it.artistName,
                trackTime = dateFormatFromMillisToMss.format(it.trackTimeMillis),
                artworkUrl100 = it.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"),
                collectionName = it.collectionName,
                releaseDate = it.releaseDate?.takeIf { it.length >= 4 }?.substring(0, 4)
                    ?: "XXXX",
                primaryGenreName = it.primaryGenreName,
                country = it.country,
                previewUrl = it.previewUrl
            )
        }.toMutableList()
        return lastTracks
    }

    private companion object {
        const val LAST_TRACK_LIST_KEY = "last_track_list_key"
        const val MAX_TRACK_HISTORY = 10
    }
}
