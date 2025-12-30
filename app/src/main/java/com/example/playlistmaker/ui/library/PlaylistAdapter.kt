package com.example.playlistmaker.ui.library

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.model.library.Playlist

class PlaylistAdapter : RecyclerView.Adapter<PlaylistViewHolder>() {

    private val playlistList: MutableList<Playlist> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder =
        PlaylistViewHolder.from(parent)

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlistList[position])
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

    fun updateList(newList: MutableList<Playlist>) {
        playlistList.clear()
        playlistList.addAll(newList)
        notifyDataSetChanged()
    }
}