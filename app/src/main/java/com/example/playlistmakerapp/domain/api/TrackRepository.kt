package com.example.playlistmakerapp.domain.api

import com.example.playlistmakerapp.domain.models.Track
import com.example.playlistmakerapp.util.Resource

interface TrackRepository {
    fun search(term: String): Resource<List<Track>>
}