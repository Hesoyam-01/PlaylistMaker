package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(
    private val trackSharedPrefs: SharedPreferences
) : SearchHistoryRepository {

    override val lastTracksList = mutableListOf<Track>()
    private val gson = Gson()

    private val trackSharedPrefsListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == LAST_TRACK_LIST_KEY) {
                loadLastTracksList()
            }
        }

    init {
        loadLastTracksList()
        trackSharedPrefs.registerOnSharedPreferenceChangeListener(trackSharedPrefsListener)
    }


    override fun saveLastTracksList() {
        trackSharedPrefs.edit()
            .putString(LAST_TRACK_LIST_KEY, gson.toJson(lastTracksList))
            .apply()
    }

    override fun addToLastTracksList(track: Track) {
        lastTracksList.removeAll { it.trackId == track.trackId }
        if (lastTracksList.size >= MAX_TRACK_HISTORY) {
            lastTracksList.removeAt(9)
        }
        lastTracksList.add(0, track)
        saveLastTracksList()
    }

    override fun loadLastTracksList() {
        val jsonLastTrackList = trackSharedPrefs.getString(LAST_TRACK_LIST_KEY, null)
        val type = object : TypeToken<MutableList<Track>>() {}.type
        val loadedTracks = gson.fromJson<List<Track>>(jsonLastTrackList, type) ?: emptyList()
        lastTracksList.clear()
        lastTracksList.addAll(loadedTracks)
    }

    private companion object {
        const val MAX_TRACK_HISTORY = 10
        const val LAST_TRACK_LIST_KEY = "last_track_list_key"
    }
}
