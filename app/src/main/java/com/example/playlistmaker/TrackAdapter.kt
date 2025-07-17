package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(private val trackList: MutableList<Track>) :
    RecyclerView.Adapter<TrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context, "text", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

}