package com.example.playlistmakerapp.media.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.playlistmakerapp.media.domain.model.Playlist

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long = 0,
    val playlistName: String,
    val playlistDescription: String? = null,
    val imagePath: String? = null,
    val tracksIds: String?,
    val tracksCount: Int = 0
) {
    fun toDomain(): Playlist {
        return Playlist(
            id = playlistId,
            name = playlistName,
            description = playlistDescription,
            imagePath = imagePath,
            trackIds = tracksIds?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
            tracksCount = tracksCount
        )
    }

    companion object {
        fun fromDomain(playlist: Playlist): PlaylistEntity {
            return PlaylistEntity(
                playlistId = playlist.id,
                playlistName = playlist.name,
                playlistDescription = playlist.description,
                imagePath = playlist.imagePath,
                tracksIds = playlist.trackIds.joinToString(","),
                tracksCount = playlist.tracksCount
            )
        }
    }
}
