package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackCover = itemView.findViewById<ImageView>(R.id.track_cover)
    private val trackName = itemView.findViewById<TextView>(R.id.track_name)
    private val artistName = itemView.findViewById<TextView>(R.id.artist_name)
    private val trackTime = itemView.findViewById<TextView>(R.id.track_time)

    fun bind(model: Track) {
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.album_placeholder)
            .transform(RoundedCorners(2))
            .into(trackCover)
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackTime
    }
}