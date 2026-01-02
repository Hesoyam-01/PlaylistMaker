package com.example.playlistmaker.ui.player

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.model.library.Playlist

class AddToPlaylistAdapter(
    private val onPlaylistClicked: (Playlist) -> Unit
) :
    RecyclerView.Adapter<AddToPlaylistViewHolder>() {

    private var playlistList: List<Playlist> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddToPlaylistViewHolder =
        AddToPlaylistViewHolder.from(parent)

    override fun getItemCount(): Int {
        return playlistList.size
    }

    override fun onBindViewHolder(holder: AddToPlaylistViewHolder, position: Int) {
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