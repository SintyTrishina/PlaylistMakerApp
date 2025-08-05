package com.example.playlistmakerapp.media.data.db

import com.example.playlistmakerapp.media.data.db.entity.PlaylistEntity
import com.example.playlistmakerapp.media.data.db.entity.toDomain
import com.example.playlistmakerapp.media.data.db.entity.toPlaylistTrackEntity
import com.example.playlistmakerapp.media.domain.db.PlaylistRepository
import com.example.playlistmakerapp.media.domain.model.Playlist
import com.example.playlistmakerapp.search.data.db.AppDataBase
import com.example.playlistmakerapp.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(private val appDataBase: AppDataBase) : PlaylistRepository {

    override suspend fun addPlaylist(playlist: Playlist) {
        appDataBase.playlistDao().insertPlaylist(PlaylistEntity.fromDomain(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDataBase.playlistDao().update(PlaylistEntity.fromDomain(playlist))
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return appDataBase.playlistDao().getAllPlaylists().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return appDataBase.playlistDao().getById(id)?.toDomain()
    }

    override suspend fun deletePlaylistById(id: Long) {
        appDataBase.playlistDao().delete(id)
    }

    override suspend fun insertTrack(track: Track) {
        appDataBase.playlistTracksDao().insert(track.toPlaylistTrackEntity())
    }

    override suspend fun getPlaylists(): List<Playlist> {
        return appDataBase.playlistDao().getAllPlaylistsSync().map { it.toDomain() }
    }

    override fun getTracksByIds(trackIds: List<String>): Flow<List<Track>> {

        return appDataBase.playlistTracksDao()
            .getTracksByIds(trackIds)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override suspend fun removeTrackFromPlaylist(trackId: String, playlist: Playlist) {
        val updatedTrackIds = playlist.trackIds - trackId
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            tracksCount = updatedTrackIds.size
        )
        appDataBase.playlistDao().update(PlaylistEntity.fromDomain(updatedPlaylist))

        checkAndRemoveOrphanedTrack(trackId)
    }

    override suspend fun checkAndRemoveOrphanedTrack(trackId: String) {

        val allPlaylists = appDataBase.playlistDao().getAllPlaylistsSync()

        val isTrackUsed = allPlaylists.any { playlist ->
            playlist.tracksIds?.contains(trackId) == true
        }

        if (!isTrackUsed) {
            appDataBase.playlistTracksDao().deleteTrack(trackId)
        }
    }


}