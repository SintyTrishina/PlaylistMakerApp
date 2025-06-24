package com.example.playlistmakerapp.search.data

import com.example.playlistmakerapp.search.data.dto.TrackSearchRequest
import com.example.playlistmakerapp.search.data.dto.TrackSearchResponse
import com.example.playlistmakerapp.search.domain.api.TrackRepository
import com.example.playlistmakerapp.search.domain.models.Track
import com.example.playlistmakerapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {

    override fun search(term: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(term))

        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету", emptyList()))
            }
            200 -> {
                emit(Resource.Success((response as TrackSearchResponse).results.map {
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
                }))
            }
            else -> {
                emit(Resource.Error("Ошибка сервера", emptyList()))
            }
        }
    }
}