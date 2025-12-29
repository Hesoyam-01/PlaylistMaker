package com.example.playlistmaker.ui.search

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.model.search.Track

class TrackAdapter(
    private val onTrackClick: (Track) -> Unit
) :
    RecyclerView.Adapter<TrackViewHolder>() {

    private val trackList: MutableList<Track> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder = TrackViewHolder.from(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            onTrackClick(trackList[position])
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    fun updateList(newList: MutableList<Track>) {
        trackList.clear()
        trackList.addAll(newList)
        notifyDataSetChanged()
    }

    fun clearTrackList() {
        trackList.clear()
        notifyDataSetChanged()
    }

}



