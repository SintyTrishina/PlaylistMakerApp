package com.example.playlistmakerapp.search.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmakerapp.search.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("DELETE FROM track_table WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Int)

    @Query("SELECT * FROM track_table ORDER BY addedDate DESC")
    fun getTracks() : Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM track_table")
    suspend fun getTracksId() : List<Int>



}