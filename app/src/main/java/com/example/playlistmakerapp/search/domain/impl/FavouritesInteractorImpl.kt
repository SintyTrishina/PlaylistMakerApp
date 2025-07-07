package com.example.playlistmakerapp.search.domain.impl

import com.example.playlistmakerapp.search.domain.db.FavouritesInteractor
import com.example.playlistmakerapp.search.domain.db.FavouritesRepository
import com.example.playlistmakerapp.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class FavouritesInteractorImpl(private val favouritesRepository: FavouritesRepository) :
    FavouritesInteractor {
    override suspend fun addFavouriteTrack(track: Track) {
        favouritesRepository.addFavouriteTrack(track)
    }

    override suspend fun deleteFavouriteTrack(trackId: Int) {
        favouritesRepository.deleteFavouriteTrack(trackId)
    }

    override fun getFavourites(): Flow<List<Track>> {
        return favouritesRepository.getFavourites()
    }

    override fun getIdsTracks(searchList: List<Track>): Flow<List<Track>> = flow {
        val filteredList = searchList.let { trackList ->
            val favoritesId = favouritesRepository.getIdFavoriteTracks().first().toSet()
            trackList.map { track ->
                track.copy(isFavourite = favoritesId.contains(track.trackId))
            }
        }
        emit(filteredList)
    }

    override suspend fun isFavourite(trackId: Int): Boolean {
        return favouritesRepository.getIdFavoriteTracks()
            .first()
            .contains(trackId)
    }
}