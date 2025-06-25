package com.example.playlistmakerapp.search.domain.api

import com.example.playlistmakerapp.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackInteractor {
    fun search(term: String): Flow<Pair<List<Track>?, String?>>
}