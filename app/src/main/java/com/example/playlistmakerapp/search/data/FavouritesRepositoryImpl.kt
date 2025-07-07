package com.example.playlistmakerapp.search.data

import com.example.playlistmakerapp.search.data.db.AppDataBase
import com.example.playlistmakerapp.search.data.db.entity.TrackEntity
import com.example.playlistmakerapp.search.domain.db.FavouritesRepository
import com.example.playlistmakerapp.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FavouritesRepositoryImpl(
    private val appDataBase: AppDataBase,
    private val trackDbConvertor: TrackDbConvertor,
) : FavouritesRepository {

    override suspend fun addFavouriteTrack(track: Track) {
        appDataBase.trackDao().insertTrack(convertToTrackEntity(track))
    }

    override suspend fun deleteFavouriteTrack(trackId: Int) {
        appDataBase.trackDao().deleteTrack(trackId)
    }

    override fun getFavourites(): Flow<List<Track>>{
        return appDataBase.trackDao().getTracks()
            .map { entities -> convertFromTrackEntity(entities) }
    }

    override fun getIdFavoriteTracks(): Flow<List<Int>> = flow {
        val favoriteId = appDataBase.trackDao().getTracksId()
        emit(favoriteId)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>) : List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

    private fun convertToTrackEntity(track: Track): TrackEntity {
        return trackDbConvertor.map(track)
    }
}