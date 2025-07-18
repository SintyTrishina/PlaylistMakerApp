package com.example.playlistmakerapp.media.domain.model

import android.net.Uri

data class Playlist(
    val id: Long,
    val name: String,
    val description: String?,
    val imagePath: Uri?,
    val trackIds: List<Long>,
    val tracksCount: Int
)
