package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Resource
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<MutableList<TrackDto>>
) : SearchHistoryRepository {

    private companion object {
        const val SEARCH_HISTORY_KEY = "search_history_key"
        const val MAX_TRACK_HISTORY = 10
    }

    private val lastTracksDtoList = mutableListOf<TrackDto>()

    private val gson = Gson()

    private val trackSharedPrefsListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == SEARCH_HISTORY_KEY) {
                loadLastTracksListDtoFromSharedPrefs()
            }
        }

    init {
        loadLastTracksListDtoFromSharedPrefs()
        storage.registerListener(trackSharedPrefsListener)
    }

    private val dateFormatFromMillisToMss by lazy { SimpleDateFormat("m:ss", Locale.getDefault()) }
    private val dateFormatFromMssToMillis by lazy {
        fun(durationStr: String): Long {
            val date = dateFormatFromMillisToMss.parse(durationStr)
            return date?.time ?: 0
        }
    }



    override fun putLastTracksDtoListIntoSharedPrefs() {
        /*trackSharedPrefs.edit()
            .putString(SEARCH_HISTORY_KEY, gson.toJson(lastTracksDtoList))
            .apply()*/
        storage.storeData(lastTracksDtoList)
    }

    override fun addToLastTracksDtoList(track: Track) {
        val trackDto = fromTrackToTrackDto(track)
        lastTracksDtoList.removeAll { it.trackId == trackDto.trackId }
        if (lastTracksDtoList.size >= MAX_TRACK_HISTORY) {
            lastTracksDtoList.removeAt(9)
        }
        lastTracksDtoList.add(0, trackDto)
        putLastTracksDtoListIntoSharedPrefs()
    }

    override fun loadLastTracksListDtoFromSharedPrefs() {
        /*val jsonLastTrackList = trackSharedPrefs.getString(SEARCH_HISTORY_KEY, null)
        val type = object : TypeToken<MutableList<TrackDto>>() {}.type
        val loadedTracks = gson.fromJson<List<TrackDto>>(jsonLastTrackList, type) ?: emptyList()*/
        lastTracksDtoList.clear()
        storage.getData()?.let { lastTracksDtoList.addAll(it) }
    }

    override fun clearLastTracksDtoList() {
        lastTracksDtoList.clear()
    }

    override fun getSearchHistory(): Resource<MutableList<Track>> {
        val lastTracksList = lastTracksDtoList.map { trackDto ->
            fromTrackDtoToTrack(trackDto)
        }.toMutableList()
        return Resource.Success(lastTracksList)
    }

    private fun fromTrackDtoToTrack(trackDto: TrackDto): Track {
        val track = Track(
                trackId = trackDto.trackId,
                trackName = trackDto.trackName,
                artistName = trackDto.artistName,
                trackTime = dateFormatFromMillisToMss.format(trackDto.trackTimeMillis),
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
            trackTimeMillis = dateFormatFromMssToMillis(track.trackTime),
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
        return trackDto
    }

}
