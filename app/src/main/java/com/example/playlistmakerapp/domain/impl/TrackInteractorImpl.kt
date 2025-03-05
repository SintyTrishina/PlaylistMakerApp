package com.example.playlistmakerapp.domain.impl

import com.example.playlistmakerapp.domain.api.TrackInteractor
import com.example.playlistmakerapp.domain.api.TrackRepository
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun search(term: String, consumer: TrackInteractor.TrackConsumer) {
        executor.execute {
            consumer.consume(repository.search(term))
        }
    }
}