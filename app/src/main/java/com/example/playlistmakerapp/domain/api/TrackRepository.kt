package com.example.playlistmakerapp.domain.api

import com.example.playlistmakerapp.domain.models.Track

interface TrackRepository {
    fun search(term: String): List<Track>
}