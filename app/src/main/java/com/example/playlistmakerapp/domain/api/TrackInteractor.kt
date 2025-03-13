package com.example.playlistmakerapp.domain.api

import com.example.playlistmakerapp.domain.models.Track

interface TrackInteractor {
    fun search(term: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(data: List<Track>?, errorMessage: String?)
    }
}