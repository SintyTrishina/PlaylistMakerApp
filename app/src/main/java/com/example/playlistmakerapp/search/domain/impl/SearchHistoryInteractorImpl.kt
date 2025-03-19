package com.example.playlistmakerapp.search.domain.impl

import com.example.playlistmakerapp.search.domain.api.SearchHistoryInteractor
import com.example.playlistmakerapp.search.domain.api.SearchHistoryRepository
import com.example.playlistmakerapp.search.domain.models.Track

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {

    override fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override fun saveTrackToPrefs(track: Track, timestamp: Long) {
        repository.saveTrackToPrefs(track, timestamp)
    }

    override fun getHistory(): ArrayList<Track> {
        return repository.getHistory()
    }

    override fun cleanHistory() {
        repository.cleanHistory()
    }

    override fun loadHistoryFromPrefs() {
        repository.loadHistoryFromPrefs()
    }
}