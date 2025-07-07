package com.example.playlistmakerapp.media.ui.viewmodel

import com.example.playlistmakerapp.search.domain.models.Track

sealed interface FavouritesState {

    data object Empty : FavouritesState

    data class Content(val favouritesList: List<Track>) : FavouritesState
}