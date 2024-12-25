package com.example.playlistmakerapp

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter : RecyclerView.Adapter<TrackViewHolder>() {

    private var tracks: ArrayList<Track> = ArrayList()
    private lateinit var searchHistory: SearchHistory

    fun initSharedPrefs(sharedPrefs: SharedPreferences) {
        searchHistory = SearchHistory(sharedPrefs)
    }

    fun updateTracks(newTracks: ArrayList<Track>) {
        tracks = newTracks
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
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            val currentTrack = tracks[position]
                searchHistory.addTrack(currentTrack) //нажимая на элемент ui добавляем трек в историю
        }
    }
}