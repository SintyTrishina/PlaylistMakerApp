package com.example.playlistmakerapp.data

import com.example.playlistmakerapp.data.dto.TrackSearchRequest
import com.example.playlistmakerapp.data.dto.TrackSearchResponse
import com.example.playlistmakerapp.domain.api.TrackRepository
import com.example.playlistmakerapp.domain.models.Track

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {

    override fun search(term: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(term))
        if (response.resultCode == 200) {
            return (response as TrackSearchResponse).results.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )
            }
        } else {
            return emptyList()
        }
    }
}