package com.example.playlistmakerapp.search.domain.di

import com.example.playlistmakerapp.search.data.TrackRepositoryImpl
import com.example.playlistmakerapp.search.domain.api.TrackRepository
import org.koin.dsl.module

val trackRepositoryModule = module {

    single<TrackRepository> {
        TrackRepositoryImpl(get())
    }

}