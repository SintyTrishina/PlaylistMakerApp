package com.example.playlistmakerapp.media.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmakerapp.media.domain.db.PlaylistInteractor
import com.example.playlistmakerapp.media.domain.model.Playlist
import com.example.playlistmakerapp.search.domain.models.Track
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

class PlaylistInfoViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _playlistState = MutableLiveData<PlaylistState>()
    val playlistState: LiveData<PlaylistState> = _playlistState

    fun loadPlaylistInfo(playlistId: Long) {
        viewModelScope.launch {
            try {
                val playlist = playlistInteractor.getPlaylistById(playlistId)
                if (playlist == null) {
                    _playlistState.value = PlaylistState.Error("Playlist not found")
                    return@launch
                }

                val tracks = playlistInteractor.getTracksByIds(playlist.trackIds).first()
                val totalDuration = tracks.sumOf { it.trackTimeMillis ?: 0 }
                val formattedDuration = formatDuration(totalDuration)

                _playlistState.value = PlaylistState.Content(
                    playlist = playlist.copy(tracksCount = tracks.size),
                    tracks = tracks,
                    duration = formattedDuration,
                    tracksCount = tracks.size
                )
            } catch (e: Exception) {
                _playlistState.value = PlaylistState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun formatDuration(millis: Long): String {
        val duration = millis.milliseconds
        return String.format(
            Locale.getDefault(),
            "%d мин",
            duration.inWholeMinutes
        )
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            try {
                playlistInteractor.deletePlaylistById(playlist.id)
            } catch (e: Exception) {
                _playlistState.postValue(PlaylistState.Error("Ошибка при удалении плейлиста: ${e.message}"))
            }
        }
    }

    fun removeTrackFromPlaylist(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            try {
                playlistInteractor.removeTrackFromPlaylist(
                    trackId = track.trackId.toString(),
                    playlist = playlist
                )
                loadPlaylistInfo(playlist.id)
            } catch (e: Exception) {
                _playlistState.postValue(PlaylistState.Error("Ошибка при удалении трека: ${e.message}"))
            }
        }
    }

}

sealed class PlaylistState {
    data class Content(
        val playlist: Playlist,
        val tracks: List<Track>,
        val duration: String,
        val tracksCount: Int
    ) : PlaylistState()

    data class Error(val message: String) : PlaylistState()
}