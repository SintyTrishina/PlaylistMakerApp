package com.example.playlistmakerapp.search.domain.api

import com.example.playlistmakerapp.search.domain.models.Track

interface SearchHistoryInteractor {

    fun addTrack(track: Track)
    fun saveTrackToPrefs(track: Track, timestamp: Long)
    fun getHistory(): ArrayList<Track>
    fun cleanHistory()
    fun loadHistoryFromPrefs()

}