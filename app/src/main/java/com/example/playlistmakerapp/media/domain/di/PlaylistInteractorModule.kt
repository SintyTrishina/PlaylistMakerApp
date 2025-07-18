package com.example.playlistmakerapp.media.domain.di

import com.example.playlistmakerapp.media.domain.db.PlaylistInteractor
import com.example.playlistmakerapp.media.domain.impl.PlaylistInteractorImpl
import org.koin.dsl.module

val playlistInteractorModule = module {

    single<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }
}