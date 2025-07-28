package com.example.playlistmakerapp.player.ui

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.media.domain.model.Playlist

class PlaylistBehaviorViewHolder(itemView: View, val onPlaylistClickListener: (Playlist) -> Unit) :
    RecyclerView.ViewHolder(itemView) {

    private val playlistPoster: ImageView = itemView.findViewById(R.id.poster)
    private val playlistName: TextView = itemView.findViewById(R.id.behavior_name)
    private val playlistTracksCount: TextView = itemView.findViewById(R.id.tracksCount)


    fun bind(playlist: Playlist) {
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onPlaylistClickListener(playlist)
            }
        }
        playlistName.text = playlist.name
        val cornerRadius = dpToPx(2f, itemView.context)
        playlistTracksCount.text = convertText(playlist.tracksCount)

        Glide.with(playlistPoster)
            .load(playlist.imagePath)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .transform(CenterCrop(),RoundedCorners(cornerRadius))
            .into(playlistPoster)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    private fun convertText(count: Int): String {
        return when {
            count % 100 in 11..14 -> "$count треков"
            count % 10 == 1 -> "$count трек"
            count % 10 in 2..4 -> "$count трека"
            else -> "$count треков"
        }
    }
}