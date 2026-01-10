package com.example.playlistmaker.ui.library

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.model.library.Playlist

class PlaylistAdapter(
    private val onPlaylistClicked: (Playlist) -> Unit
) :
    RecyclerView.Adapter<PlaylistViewHolder>() {

    private var playlistList: List<Playlist> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder =
        PlaylistViewHolder.from(parent)

    override fun getItemCount(): Int {
        return playlistList.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlistList[position])
        holder.itemView.setOnClickListener {
            onPlaylistClicked(playlistList[position])
        }
    }

    fun updateList(newList: List<Playlist>) {
        playlistList = newList
        notifyDataSetChanged()
    }
}