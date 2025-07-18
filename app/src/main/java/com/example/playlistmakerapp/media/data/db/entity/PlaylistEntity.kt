package com.example.playlistmakerapp.media.data.db.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.playlistmakerapp.media.domain.model.Playlist
import com.google.gson.Gson

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long = 0,
    val playlistName: String,
    val playlistDescription: String? = null,
    val imagePath: String? = null,
    val tracksIds: String? = null,
    val tracksCount: Int = 0
) {
    fun toDomain(): Playlist {
        return Playlist(
            playlistId,
            playlistName,
            playlistDescription,
            imagePath?.let { Uri.parse(it) },
            tracksIds?.let {
                Gson().fromJson(it, Array<Long>::class.java).toList()
            } ?: emptyList(),
            tracksCount
        )
    }
    companion object {
        fun fromDomain(playlist: Playlist): PlaylistEntity {
            return PlaylistEntity(
                playlistId = playlist.id,
                playlistName = playlist.name,
                playlistDescription = playlist.description,
                imagePath = playlist.imagePath?.toString(),
                tracksIds = Gson().toJson(playlist.trackIds),
                tracksCount = playlist.trackIds.size
            )
        }
    }
}
