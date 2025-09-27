package com.example.playlistmaker.ui.search

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackAdapter(
    private var trackList: MutableList<Track>,
    private val onItemClick: (Track) -> Unit
) :
    RecyclerView.Adapter<TrackViewHolder>() {

    private companion object {
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder = TrackViewHolder.from(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            if (clickDebounce()) onItemClick(trackList[position])
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    fun updateList(newList: MutableList<Track>) {
        trackList = newList
        notifyDataSetChanged()
    }

    fun clearTrackList() {
        trackList.clear()
        notifyDataSetChanged()
    }

    private var isClickAllowed = true
    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}



