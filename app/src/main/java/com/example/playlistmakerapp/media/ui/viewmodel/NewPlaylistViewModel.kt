package com.example.playlistmakerapp.media.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmakerapp.media.domain.db.PlaylistInteractor
import com.example.playlistmakerapp.media.domain.model.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    fun savePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            try {
                playlistInteractor.addPlaylist(playlist)
            } catch (e: Exception) {
                Log.e("NewPlaylistVM", "Ошибка сохранения", e)
            }
        }
    }
}