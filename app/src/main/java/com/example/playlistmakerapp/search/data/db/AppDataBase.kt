package com.example.playlistmakerapp.search.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmakerapp.media.data.db.dao.PlaylistDao
import com.example.playlistmakerapp.media.data.db.entity.PlaylistEntity
import com.example.playlistmakerapp.search.data.db.dao.TrackDao
import com.example.playlistmakerapp.search.data.db.entity.TrackEntity


@Database(version = 1, entities = [TrackEntity::class, PlaylistEntity::class])
abstract class AppDataBase : RoomDatabase() {
    abstract fun trackDao() : TrackDao
    abstract fun playlistDao() : PlaylistDao
}