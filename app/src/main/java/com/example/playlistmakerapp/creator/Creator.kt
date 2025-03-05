package com.example.playlistmakerapp.creator

import com.example.playlistmakerapp.data.TrackRepositoryImpl
import com.example.playlistmakerapp.data.network.RetrofitNetworkClient
import com.example.playlistmakerapp.domain.api.TrackInteractor
import com.example.playlistmakerapp.domain.api.TrackRepository
import com.example.playlistmakerapp.domain.impl.TrackInteractorImpl

object Creator {
    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }
}