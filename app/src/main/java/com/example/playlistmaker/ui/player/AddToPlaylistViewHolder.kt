package com.example.playlistmaker.ui.player

import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.AddToPlaylistViewBinding
import com.example.playlistmaker.domain.model.library.Playlist
import java.io.File

class AddToPlaylistViewHolder (private val binding: AddToPlaylistViewBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Playlist) {
        val filePath = File(itemView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlistsCovers")
        val file = if (model.coverFilePath != null) File(filePath, model.coverFilePath) else null

        binding.apply {
            Glide.with(root)
                .load(file)
                .placeholder(R.drawable.ic_cover_placeholder_45)
                .into(playlistCover)
            playlistName.text = model.playlistName
            trackCount.text = root.resources.getQuantityString(R.plurals.tracks_count, model.trackCount, model.trackCount)
        }
    }

    companion object {
        fun from(parent: ViewGroup): AddToPlaylistViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = AddToPlaylistViewBinding.inflate(inflater, parent, false)
            return AddToPlaylistViewHolder(binding)
        }
    }
}