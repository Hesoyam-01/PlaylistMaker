package com.example.playlistmaker.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.domain.models.search.Track

class TrackViewHolder (private val binding: TrackViewBinding) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): TrackViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = TrackViewBinding.inflate(inflater, parent, false)
            return TrackViewHolder(binding)
        }
    }

    fun bind(model: Track) {
        binding.apply {
            Glide.with(root)
                .load(model.artworkUrl100)
                .placeholder(R.drawable.album_placeholder)
                .into(trackCover)
            trackName.text = model.trackName
            artistName.text = model.artistName
            trackTime.text = model.trackTime
        }
    }
}