package com.example.playlistmakerapp.search.data

import com.example.playlistmakerapp.search.data.db.AppDataBase
import com.example.playlistmakerapp.search.data.dto.TrackSearchRequest
import com.example.playlistmakerapp.search.data.dto.TrackSearchResponse
import com.example.playlistmakerapp.search.domain.api.TrackRepository
import com.example.playlistmakerapp.search.domain.models.Track
import com.example.playlistmakerapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDataBase: AppDataBase
) : TrackRepository {

    override fun search(term: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(term))

        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету", emptyList()))
            }

            200 -> {
                val idFavoriteTracks = appDataBase.trackDao().getTracksId().toSet()
                with(response as TrackSearchResponse) {
                    val data = results.map {
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
                            it.previewUrl,
                            isFavourite = idFavoriteTracks.contains(it.trackId),
                        )
                    }
                    emit(Resource.Success(data))
                }
            }

            else -> {
                emit(Resource.Error("Ошибка сервера", emptyList()))
            }
        }
    }
}