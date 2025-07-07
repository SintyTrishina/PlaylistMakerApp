package com.example.playlistmakerapp.search.data

import com.example.playlistmakerapp.search.data.db.entity.TrackEntity
import com.example.playlistmakerapp.search.data.dto.TrackDto
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
            track.artworkUrl100
        )
    }

    fun map(trackDto: TrackDto): TrackEntity {
        return TrackEntity(
            trackDto.trackId,
            trackDto.trackName,
            trackDto.artistName,
            trackDto.trackTimeMillis,
            trackDto.previewUrl,
            trackDto.primaryGenreName,
            trackDto.releaseDate,
            trackDto.country,
            trackDto.collectionName,
            trackDto.artworkUrl100
        )
    }

    fun map(trackEntity: TrackEntity): Track {
        return Track(
            trackEntity.trackId,
            trackEntity.trackName,
            trackEntity.artistName,
            trackEntity.trackTimeMillis,
            trackEntity.previewUrl,
            trackEntity.primaryGenreName,
            trackEntity.releaseDate,
            trackEntity.country,
            trackEntity.collectionName,
            trackEntity.artworkUrl100
        )
    }
}