package com.example.playlistmakerapp.search.domain.di

import com.example.playlistmakerapp.search.domain.api.SearchHistoryInteractor
import com.example.playlistmakerapp.search.domain.api.TrackInteractor
import com.example.playlistmakerapp.search.domain.db.FavouritesInteractor
import com.example.playlistmakerapp.search.domain.impl.FavouritesInteractorImpl
import com.example.playlistmakerapp.search.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmakerapp.search.domain.impl.TrackInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<TrackInteractor> {
        TrackInteractorImpl(get())
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    single<FavouritesInteractor> {
        FavouritesInteractorImpl(get())
    }
}