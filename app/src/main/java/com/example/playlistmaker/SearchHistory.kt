package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val trackSharedPrefs: SharedPreferences,
                    private val lastTrackList: MutableList<Track>,
                    private val context: Context,
                    private val visibilityOfLastTracks: () -> Unit) {

    val lastTrackAdapter = TrackAdapter(lastTrackList) { track ->
        Toast.makeText(context, "Выбран трек: ${track.trackName}", Toast.LENGTH_SHORT).show()
        updateLastTrackList(track)
    }

    private val trackSharedPrefsListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if (key == LAST_TRACK_LIST_KEY) {
            loadLastTrackList()
            visibilityOfLastTracks()
        }
    }

    init {
        loadLastTrackList()
        visibilityOfLastTracks()

        trackSharedPrefs.registerOnSharedPreferenceChangeListener(trackSharedPrefsListener)
    }


    fun saveLastTrackList() {
        trackSharedPrefs.edit()
            .putString(LAST_TRACK_LIST_KEY, Gson().toJson(lastTrackList))
            .apply()
    }

    fun updateLastTrackList(track: Track) {
        lastTrackList.removeAll { it.trackId == track.trackId }
        if (lastTrackList.size >= MAX_TRACK_HISTORY) {
            lastTrackList.removeAt(9)
        }
        lastTrackList.add(0, track)
        saveLastTrackList()
    }

    private fun loadLastTrackList() {
        val jsonLastTrackList = trackSharedPrefs.getString(LAST_TRACK_LIST_KEY, null)
        val type = object : TypeToken<MutableList<Track>>() {}.type
        val loadedTracks = Gson().fromJson<List<Track>>(jsonLastTrackList, type) ?: emptyList()
        lastTrackList.clear()
        lastTrackList.addAll(loadedTracks)
        lastTrackAdapter.updateList(lastTrackList)
    }

    private companion object {
        const val MAX_TRACK_HISTORY = 10
        const val LAST_TRACK_LIST_KEY = "last_track_list_key"
    }
}
