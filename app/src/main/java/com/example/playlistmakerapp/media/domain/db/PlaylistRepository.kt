package com.example.playlistmakerapp.media.domain.db

import com.example.playlistmakerapp.media.domain.model.Playlist
import com.example.playlistmakerapp.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun addPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun getPlaylistById(id: Long): Playlist?

    suspend fun deletePlaylistById(id: Long)

    suspend fun insertTrack(track: Track)
    suspend fun getPlaylists(): List<Playlist>

    fun getTracksByIds(trackIds: List<String>): Flow<List<Track>>
}