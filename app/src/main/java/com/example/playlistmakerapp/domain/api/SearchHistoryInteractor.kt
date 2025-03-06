package com.example.playlistmakerapp.domain.api

import com.example.playlistmakerapp.domain.models.Track

interface SearchHistoryInteractor {

    fun addTrack(track: Track)
    fun saveTrackToPrefs(track: Track, timestamp: Long)
    fun getHistory(): ArrayList<Track>
    fun cleanHistory()
    fun loadHistoryFromPrefs()

}