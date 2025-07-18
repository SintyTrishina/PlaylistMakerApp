package com.example.playlistmakerapp.media.domain.di

import com.example.playlistmakerapp.media.data.db.PlaylistRepositoryImpl
import com.example.playlistmakerapp.media.domain.db.PlaylistRepository
import org.koin.dsl.module

val playlistRepositoryModule = module {

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get())
    }

}