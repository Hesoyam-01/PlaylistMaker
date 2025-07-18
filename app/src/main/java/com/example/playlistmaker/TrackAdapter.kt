package com.example.playlistmaker

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class TrackAdapter(private val trackList: MutableList<Track>, private val context: Context) :
    RecyclerView.Adapter<TrackViewHolder>() {

    val lastTrackList = mutableListOf<Track>()

    private val trackSharedPrefs by lazy {
        context.getSharedPreferences(TRACK_SHARED_PREFS, Context.MODE_PRIVATE)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context, "click", Toast.LENGTH_SHORT).show()
            updateLastTrackList(trackList[position])
            saveLastTrackList()
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    private fun saveLastTrackList() {
        trackSharedPrefs.edit()
            .putString(LAST_TRACK_LIST_KEY, Gson().toJson(lastTrackList))
            .apply()
    }

    private fun updateLastTrackList(track: Track) {
        lastTrackList.removeAll { it.trackId == track.trackId }
        if (lastTrackList.size >= 10) {
            lastTrackList.removeAt(9)
        }
        lastTrackList.add(0, track)
        Log.d("index", "${lastTrackList.indexOf(track)}")
        Log.d("size", "${lastTrackList.size}")
    }

    private companion object {
        const val TRACK_SHARED_PREFS = "track_shared_prefs"
        const val LAST_TRACK_LIST_KEY = "last_track_list_key"
    }

}