package com.example.playlistmakerapp.ui.search

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.domain.models.Track

class TrackAdapter(
    private val onItemClickListener: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    private var tracks: ArrayList<Track> = ArrayList()
//    private lateinit var searchHistoryRepository: SearchHistoryRepository
//
//    fun initSharedPrefs(sharedPrefs: SharedPreferences) {
//        searchHistoryRepository = Creator.createSearchHistoryRepository(this)
//    }

    fun updateTracks(tracks: List<Track>) {
        this.tracks.clear()
        this.tracks.addAll(tracks)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_list, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onItemClickListener(track)
        }
    }
}