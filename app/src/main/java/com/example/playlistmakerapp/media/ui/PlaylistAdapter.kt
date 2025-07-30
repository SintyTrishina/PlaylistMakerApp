package com.example.playlistmakerapp.media.ui

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.media.domain.model.Playlist


class PlaylistAdapter(
    private val onPlaylistClick: (Playlist) -> Unit,
    private val loadImage: (String) -> Bitmap?
) : RecyclerView.Adapter<PlaylistViewHolder>() {

    private var playlists: List<Playlist> = emptyList()

    fun submitList(newList: List<Playlist>) {
        playlists = newList.toList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.playlist_item, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist, loadImage)
        holder.itemView.setOnClickListener { onPlaylistClick(playlist) }
    }

    override fun getItemCount(): Int = playlists.size
}