package com.example.playlistmakerapp.search.domain.di

import com.example.playlistmakerapp.search.data.FavouritesRepositoryImpl
import com.example.playlistmakerapp.search.data.TrackRepositoryImpl
import com.example.playlistmakerapp.search.domain.api.TrackRepository
import com.example.playlistmakerapp.search.domain.db.FavouritesRepository
import org.koin.dsl.module

val trackRepositoryModule = module {

    single<TrackRepository> {
        TrackRepositoryImpl(get(), get())
    }

    single<FavouritesRepository> {
        FavouritesRepositoryImpl(get(), get())
    }

}