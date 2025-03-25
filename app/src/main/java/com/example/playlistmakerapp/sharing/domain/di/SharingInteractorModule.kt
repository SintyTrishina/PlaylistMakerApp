package com.example.playlistmakerapp.sharing.domain.di

import com.example.playlistmakerapp.sharing.domain.api.SharingInteractor
import com.example.playlistmakerapp.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val sharingInteractorModule = module {
    single<SharingInteractor> {
        SharingInteractorImpl(get(), get())
    }
}