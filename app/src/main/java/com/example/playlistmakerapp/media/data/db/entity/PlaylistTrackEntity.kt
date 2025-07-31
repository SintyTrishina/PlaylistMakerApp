package com.example.playlistmakerapp.media.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.playlistmakerapp.search.domain.models.Track

@Entity(tableName = "playlist_tracks_table")
data class PlaylistTrackEntity(
    @PrimaryKey val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) {
    companion object {
        fun fromDomain(track: Track): PlaylistTrackEntity {
            return PlaylistTrackEntity(
                trackId = track.trackId.toString(),
                trackName = track.trackName,
                artistName = track.artistName,
                trackTimeMillis = track.trackTimeMillis,
                artworkUrl100 = track.artworkUrl100!!,
                collectionName = track.collectionName,
                releaseDate = track.releaseDate,
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                previewUrl = track.previewUrl ?: ""
            )
        }
    }

}
fun Track.toPlaylistTrackEntity(): PlaylistTrackEntity {
    return PlaylistTrackEntity.fromDomain(this)
}

fun PlaylistTrackEntity.toDomain(): Track {
    return Track(
        this.trackId.toInt(),
        this.trackName,
        this.artistName,
        this.trackTimeMillis,
        this.artworkUrl100,
        this.collectionName,
        this.releaseDate,
        this.primaryGenreName,
        this.country,
        this.previewUrl,

        )
}