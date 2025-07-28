package com.example.playlistmakerapp.media.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmakerapp.media.domain.db.PlaylistInteractor
import com.example.playlistmakerapp.media.domain.model.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    init {
        loadPlaylists()
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            try {
                playlistInteractor.getAllPlaylists().collect { playlists ->
                    _playlists.value = playlists
                }
            } catch (e: Exception) {
                _playlists.value = emptyList()
            }
        }
    }
}