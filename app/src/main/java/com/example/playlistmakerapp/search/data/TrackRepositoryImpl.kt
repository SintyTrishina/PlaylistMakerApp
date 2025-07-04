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
    private val appDataBase: AppDataBase,
    private val trackDbConvertor: TrackDbConvertor
) : TrackRepository {

    override fun search(term: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(term))

        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету", emptyList()))
            }

            200 -> {
                val favoriteTrackIds = try {
                    appDataBase.trackDao().getTracksId()
                } catch (e: Exception) {
                    emptyList()
                }
                val tracks = (response as TrackSearchResponse).results.map { trackDto ->
                    Track(
                        trackId = trackDto.trackId,
                        trackName = trackDto.trackName,
                        artistName = trackDto.artistName,
                        trackTimeMillis = trackDto.trackTimeMillis,
                        artworkUrl100 = trackDto.artworkUrl100,
                        collectionName = trackDto.collectionName,
                        releaseDate = trackDto.releaseDate,
                        primaryGenreName = trackDto.primaryGenreName,
                        country = trackDto.country,
                        previewUrl = trackDto.previewUrl,
                        isFavourite = favoriteTrackIds.contains(trackDto.trackId)
                    )
                }
                emit(Resource.Success(tracks))
            }

            else -> {
                emit(Resource.Error("Ошибка сервера", emptyList()))
            }
        }
    }
}