package com.example.playlistmakerapp.search.domain.db

import com.example.playlistmakerapp.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavouritesInteractor {
    suspend fun addFavouriteTrack(track: Track)

    suspend fun deleteFavouriteTrack(trackId: Int)

    fun getFavourites(): Flow<List<Track>>

    fun getIdsTracks(searchList: List<Track>): Flow<List<Track>>
}