package com.example.playlistmakerapp.media.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmakerapp.media.data.db.entity.PlaylistTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTracksDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_tracks_table WHERE trackId IN (:trackIds)")
    fun getTracksByIds(trackIds: List<String>): Flow<List<PlaylistTrackEntity>>

    @Query("DELETE FROM playlist_tracks_table WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: String)

}