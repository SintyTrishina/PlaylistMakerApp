package com.example.playlistmakerapp.player.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.media.domain.model.Playlist


class PlaylistBehaviorAdapter(
    private val onPlaylistClickListener: (Playlist) -> Unit
) :
    RecyclerView.Adapter<PlaylistBehaviorViewHolder>() {

    var playlists: List<Playlist> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistBehaviorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.playlist_behavior_item, parent, false)
        return PlaylistBehaviorViewHolder(view, onPlaylistClickListener)
    }

    override fun onBindViewHolder(holder: PlaylistBehaviorViewHolder, position: Int) {
        holder.bind(
            playlists[position]
        )
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}