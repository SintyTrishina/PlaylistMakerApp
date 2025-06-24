package com.example.playlistmakerapp.search.domain.api

import com.example.playlistmakerapp.search.domain.models.Track
import com.example.playlistmakerapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun search(term: String): Flow<Resource<List<Track>>>
}