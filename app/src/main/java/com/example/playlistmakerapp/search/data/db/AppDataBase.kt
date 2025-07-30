package com.example.playlistmakerapp.search.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmakerapp.media.data.db.dao.PlaylistDao
import com.example.playlistmakerapp.media.data.db.dao.PlaylistTracksDao
import com.example.playlistmakerapp.media.data.db.entity.PlaylistEntity
import com.example.playlistmakerapp.media.data.db.entity.PlaylistTrackEntity
import com.example.playlistmakerapp.search.data.db.dao.TrackDao
import com.example.playlistmakerapp.search.data.db.entity.TrackEntity


@Database(version = 8, entities = [TrackEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class])
abstract class AppDataBase : RoomDatabase() {
    abstract fun trackDao() : TrackDao
    abstract fun playlistDao() : PlaylistDao
    abstract fun playlistTracksDao(): PlaylistTracksDao
}