package com.example.playlistmakerapp.media.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmakerapp.search.domain.db.FavouritesInteractor
import kotlinx.coroutines.launch

class FavouritesViewModel(private val favouritesInteractor: FavouritesInteractor) :
    ViewModel() {

    private val _favouritesState = MutableLiveData<FavouritesState>()
    val favouritesState: LiveData<FavouritesState> get() = _favouritesState


    init {
        viewModelScope.launch {
            getFavourites()
        }
    }

    private fun getFavourites() {
        viewModelScope.launch {
            favouritesInteractor.getFavourites().collect() { favouriteList ->
                if (favouriteList.isEmpty()) {
                    _favouritesState.value = FavouritesState.Empty
                }
                _favouritesState.value = FavouritesState.Content(favouriteList)

            }
        }
    }
}