package com.example.playlistmakerapp.search.data

import com.example.playlistmakerapp.search.data.db.entity.TrackEntity
import com.example.playlistmakerapp.search.domain.models.Track

class TrackDbConvertor {
    fun map(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.previewUrl,
            track.primaryGenreName,
            track.releaseDate,
            track.country,
            track.collectionName,
            track.artworkUrl100,
        )
    }

    fun map(track: TrackEntity): Track {
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.previewUrl,
            track.primaryGenreName,
            track.releaseDate,
            track.country,
            track.collectionName,
            track.artworkUrl100,
        )
    }
}