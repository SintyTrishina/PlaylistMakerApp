package com.example.playlistmakerapp.media.domain.impl

import com.example.playlistmakerapp.media.domain.db.PlaylistInteractor
import com.example.playlistmakerapp.media.domain.db.PlaylistRepository
import com.example.playlistmakerapp.media.domain.model.Playlist
import com.example.playlistmakerapp.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {
    override suspend fun addPlaylist(playlist: Playlist) {
        playlistRepository.addPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylists()
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return playlistRepository.getPlaylistById(id)
    }

    override suspend fun deletePlaylistById(id: Long) {
        playlistRepository.deletePlaylistById(id)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        playlistRepository.insertTrack(track)

        val updatedTrackIds = playlist.trackIds.toMutableList().apply {
            add(track.trackId.toString())
        }

        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            tracksCount = updatedTrackIds.size
        )

        playlistRepository.updatePlaylist(updatedPlaylist)
    }

    override suspend fun getPlaylists(): List<Playlist> {
        return playlistRepository.getPlaylists()
    }

    override fun getTracksByIds(trackIds: List<String>): Flow<List<Track>> {
        return playlistRepository.getTracksByIds(trackIds)
    }

    override suspend fun removeTrackFromPlaylist(trackId: String, playlist: Playlist) {
        playlistRepository.removeTrackFromPlaylist(trackId, playlist)
    }
}