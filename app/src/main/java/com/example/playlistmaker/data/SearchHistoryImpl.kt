package com.example.playlistmaker.data

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.domain.api.SearchHistory
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.ui.search.TrackAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryImpl(
    private val context: Context,
    private val visibilityOfLastTracks: () -> Unit
) : SearchHistory {

    override val lastTracksList = mutableListOf<Track>()
    private val handler = Handler(Looper.getMainLooper())
    private val gson = Gson()
    private val trackSharedPrefs =
        context.getSharedPreferences(TRACK_SHARED_PREFS, Context.MODE_PRIVATE)

    val lastTrackAdapter = TrackAdapter(lastTracksList) { track ->
        val trackIntent = Intent(context, PlayerActivity::class.java)
        trackIntent.putExtra("TRACK_COVER", track.artworkUrl100)
        trackIntent.putExtra("TRACK_NAME", track.trackName)
        trackIntent.putExtra("ARTIST_NAME", track.artistName)
        trackIntent.putExtra("TRACK_TIME", track.trackTime)
        trackIntent.putExtra("ALBUM_NAME", track.collectionName)
        trackIntent.putExtra("RELEASE_DATE", track.releaseDate)
        trackIntent.putExtra("GENRE_NAME", track.primaryGenreName)
        trackIntent.putExtra("COUNTRY", track.country)
        trackIntent.putExtra("PREVIEW_URL", track.previewUrl)
        context.startActivity(trackIntent)
        val updateHistoryRunnable = Runnable { addToLastTrackList(track) }
        handler.postDelayed(updateHistoryRunnable, HISTORY_UPDATE_DELAY)
    }

    private val trackSharedPrefsListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
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


    override fun saveLastTrackList() {
        trackSharedPrefs.edit()
            .putString(LAST_TRACK_LIST_KEY, gson.toJson(lastTracksList))
            .apply()
    }

    override fun addToLastTrackList(track: Track) {
        lastTracksList.removeAll { it.trackId == track.trackId }
        if (lastTracksList.size >= MAX_TRACK_HISTORY) {
            lastTracksList.removeAt(9)
        }
        lastTracksList.add(0, track)
        saveLastTrackList()
    }

    override fun loadLastTrackList() {
        val jsonLastTrackList = trackSharedPrefs.getString(LAST_TRACK_LIST_KEY, null)
        val type = object : TypeToken<MutableList<Track>>() {}.type
        val loadedTracks = Gson().fromJson<List<Track>>(jsonLastTrackList, type) ?: emptyList()
        lastTracksList.clear()
        lastTracksList.addAll(loadedTracks)
        lastTrackAdapter.updateList(lastTracksList)
    }

    private companion object {
        const val MAX_TRACK_HISTORY = 10
        const val HISTORY_UPDATE_DELAY = 600L
        const val LAST_TRACK_LIST_KEY = "last_track_list_key"
        const val TRACK_SHARED_PREFS = "track_shared_prefs"
    }
}
