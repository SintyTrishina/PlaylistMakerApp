package com.example.playlistmakerapp.media.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistsViewModel(private val playlistId: Int): ViewModel() {

    private val _playlistIdLiveData = MutableLiveData(playlistId)
    val playlistIdLiveData:LiveData<Int> get()=_playlistIdLiveData

}