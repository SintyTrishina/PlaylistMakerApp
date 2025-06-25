package com.example.playlistmakerapp.search.domain.impl

import com.example.playlistmakerapp.search.domain.api.TrackInteractor
import com.example.playlistmakerapp.search.domain.api.TrackRepository
import com.example.playlistmakerapp.search.domain.models.Track
import com.example.playlistmakerapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    override fun search(term: String): Flow<Pair<List<Track>?, String?>> {
        return repository.search(term).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}
