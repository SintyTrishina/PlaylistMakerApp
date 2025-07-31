package com.example.playlistmakerapp.player.ui

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.media.domain.model.Playlist


class PlaylistBehaviorAdapter(
    private val onPlaylistClickListener: (Playlist) -> Unit,
    private val loadImage: (String?) -> Bitmap?
) : RecyclerView.Adapter<PlaylistBehaviorViewHolder>() {

    var playlists: List<Playlist> = emptyList()
        set(value) {
            field = value.toList()
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistBehaviorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.playlist_behavior_item, parent, false)
        return PlaylistBehaviorViewHolder(view, onPlaylistClickListener, loadImage)
    }

    override fun onBindViewHolder(holder: PlaylistBehaviorViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size
}