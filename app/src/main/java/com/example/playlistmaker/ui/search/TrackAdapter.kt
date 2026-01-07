package com.example.playlistmaker.ui.search

import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.model.search.Track

class TrackAdapter(
    private val onTrackClick: (Track) -> Unit,
    private val onTrackLongClick: (Int) -> Unit
) :
    RecyclerView.Adapter<TrackViewHolder>() {

    private var trackList: List<Track> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder.from(parent)

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            onTrackClick(trackList[position])
        }
        holder.itemView.setOnLongClickListener {
            onTrackLongClick(trackList[position].trackId)
            true
        }
    }

    fun updateList(newList: List<Track>) {
        trackList = newList
        notifyDataSetChanged()
    }

    fun clearTrackList() {
        trackList = emptyList()
        notifyDataSetChanged()
    }

}



