package com.example.playlistmakerapp.search.domain.impl

import com.example.playlistmakerapp.search.domain.db.FavouritesInteractor
import com.example.playlistmakerapp.search.domain.db.FavouritesRepository
import com.example.playlistmakerapp.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavouritesInteractorImpl(private val favouritesRepository: FavouritesRepository) :
    FavouritesInteractor {
    override suspend fun addFavouriteTrack(track: Track) {
        favouritesRepository.addFavouriteTrack(track)
    }

    override suspend fun deleteFavouriteTrack(track: Track) {
        favouritesRepository.deleteFavouriteTrack(track)
    }

    override fun getFavourites(): Flow<List<Track>> {
        return favouritesRepository.getFavourites()
    }
}