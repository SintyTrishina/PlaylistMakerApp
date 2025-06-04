package com.example.playlistmakerapp.media.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavouritesViewModel(trackId: Int) : ViewModel() {
    private val _trackIdLiveData = MutableLiveData(trackId)
    val trackIdLiveData:LiveData<Int> get() = _trackIdLiveData
}