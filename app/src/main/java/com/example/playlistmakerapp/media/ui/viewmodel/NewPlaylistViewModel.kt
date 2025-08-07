package com.example.playlistmakerapp.media.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmakerapp.media.domain.db.PlaylistInteractor
import com.example.playlistmakerapp.media.domain.model.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _screenState = MutableLiveData<NewPlaylistState>()
    val screenState: LiveData<NewPlaylistState> = _screenState

    private var originalPlaylist: Playlist? = null

    fun initPlaylist(playlistId: Long) {
        if (playlistId != 0L) {
            viewModelScope.launch {
                originalPlaylist = playlistInteractor.getPlaylistById(playlistId)
                originalPlaylist?.let {
                    _screenState.value = NewPlaylistState.EditState(
                        title = "Редактировать",
                        buttonText = "Сохранить",
                        playlist = it
                    )
                }
            }
        } else {
            _screenState.value = NewPlaylistState.CreateState(
                title = "Создание плейлиста",
                buttonText = "Создать"
            )
        }
    }

    fun savePlaylist(
        name: String,
        description: String?,
        imagePath: String?
    ) {
        viewModelScope.launch {
            if (originalPlaylist != null) {
                val updatedPlaylist = originalPlaylist!!.copy(
                    name = name,
                    description = description,
                    imagePath = imagePath
                )
                playlistInteractor.updatePlaylist(updatedPlaylist)
            } else {
                val newPlaylist = Playlist(
                    id = 0,
                    name = name,
                    description = description,
                    imagePath = imagePath,
                    trackIds = emptyList(),
                    tracksCount = 0
                )
                playlistInteractor.addPlaylist(newPlaylist)
            }
        }
    }

    fun getOriginalPlaylistName(): String? = originalPlaylist?.name
    fun getOriginalPlaylistDescription(): String? = originalPlaylist?.description
}

sealed class NewPlaylistState {
    abstract val title: String
    abstract val buttonText: String

    data class CreateState(
        override val title: String,
        override val buttonText: String
    ) : NewPlaylistState()

    data class EditState(
        override val title: String,
        override val buttonText: String,
        val playlist: Playlist
    ) : NewPlaylistState()
}