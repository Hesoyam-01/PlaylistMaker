package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.ui.search.TrackAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Locale

class SearchHistory(private val trackSharedPrefs: SharedPreferences,
                    private val lastTrackList: MutableList<Track>,
                    private val context: Context,
                    private val visibilityOfLastTracks: () -> Unit) {

    private val handler = Handler(Looper.getMainLooper())
    val gson = Gson()

    val lastTrackAdapter = TrackAdapter(lastTrackList) { track ->
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
        val updateHistoryRunnable = Runnable { updateLastTrackList(track) }
        handler.postDelayed(updateHistoryRunnable, HISTORY_UPDATE_DELAY)
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
            .putString(LAST_TRACK_LIST_KEY, gson.toJson(lastTrackList))
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
        const val HISTORY_UPDATE_DELAY = 600L
    }
}
