package com.example.playlistmakerapp.media.ui

import android.content.Context
import android.graphics.Bitmap
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

class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val playlistPoster: ImageView = itemView.findViewById(R.id.poster)
    private val playlistName: TextView = itemView.findViewById(R.id.playlistName)
    private val tracksCount: TextView = itemView.findViewById(R.id.tracksCount)

    fun bind(item: Playlist, loadImage: (String) -> Bitmap?) {
        playlistName.text = item.name
        tracksCount.text = convertText(item.tracksCount)

        item.imagePath?.let { path ->
            loadImage(path)?.let { bitmap ->
                val cornerRadius = dpToPx(8f, itemView.context)
                Glide.with(itemView)
                    .load(bitmap)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .transform(CenterCrop(), RoundedCorners(cornerRadius))
                    .into(playlistPoster)
            } ?: run {
                playlistPoster.setImageResource(R.drawable.placeholder)
            }
        } ?: run {
            playlistPoster.setImageResource(R.drawable.placeholder)
        }
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