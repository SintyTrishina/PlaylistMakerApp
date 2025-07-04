package com.example.playlistmakerapp.search.domain.db

import com.example.playlistmakerapp.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavouritesInteractor {
    suspend fun addFavouriteTrack(track: Track)

    suspend fun deleteFavouriteTrack(track: Track)

    fun getFavourites() : Flow<List<Track>>
}