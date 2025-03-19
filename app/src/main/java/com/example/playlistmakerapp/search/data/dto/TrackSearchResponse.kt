package com.example.playlistmakerapp.search.data.dto

data class TrackSearchResponse(
    val resultCount: Int,
    val results: List<TrackDto>,
) : Response()