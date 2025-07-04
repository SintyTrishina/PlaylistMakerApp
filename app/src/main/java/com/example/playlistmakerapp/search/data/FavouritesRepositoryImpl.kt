package com.example.playlistmakerapp.search.data

import com.example.playlistmakerapp.search.data.db.AppDataBase
import com.example.playlistmakerapp.search.data.db.entity.TrackEntity
import com.example.playlistmakerapp.search.domain.db.FavouritesRepository
import com.example.playlistmakerapp.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavouritesRepositoryImpl(
    private val appDataBase: AppDataBase,
    private val trackDbConvertor: TrackDbConvertor,
) : FavouritesRepository {

    override suspend fun addFavouriteTrack(track: Track) {
        appDataBase.trackDao().insertTrack(trackDbConvertor.map(track))
    }

    override suspend fun deleteFavouriteTrack(track: Track) {
        appDataBase.trackDao().deleteTrack(trackDbConvertor.map(track))
    }

    override fun getFavourites(): Flow<List<Track>> = flow {
        val tracks = appDataBase.trackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>) : List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}