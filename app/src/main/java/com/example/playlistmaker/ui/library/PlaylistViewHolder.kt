package com.example.playlistmaker.ui.library

import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistViewBinding
import com.example.playlistmaker.domain.model.library.Playlist
import java.io.File

class PlaylistViewHolder (private val binding: PlaylistViewBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Playlist) {
        val filePath = File(itemView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlistsCovers")
        val file = if (model.coverFilePath != null) File(filePath, model.coverFilePath) else null

        binding.apply {
            Glide.with(root)
                .load(file)
                .placeholder(R.drawable.ic_playlist_cover_placeholder_160)
                .into(playlistCover)
            playlistName.text = model.playlistName
            trackCount.text = root.resources.getQuantityString(R.plurals.tracks_count, model.trackCount, model.trackCount)
        }
    }

    companion object {
        fun from(parent: ViewGroup): PlaylistViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = PlaylistViewBinding.inflate(inflater, parent, false)
            return PlaylistViewHolder(binding)
        }
    }
}