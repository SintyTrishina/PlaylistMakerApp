package com.example.playlistmakerapp.search.domain.api

import com.example.playlistmakerapp.search.domain.models.Track
import com.example.playlistmakerapp.util.Resource

interface TrackRepository {
    fun search(term: String): Resource<List<Track>>
}