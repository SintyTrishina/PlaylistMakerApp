package com.example.playlistmakerapp.sharing.data.di

import com.example.playlistmakerapp.sharing.data.impl.SharingResourcesProviderImpl
import com.example.playlistmakerapp.sharing.domain.SharingResourcesProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharingResourcesProviderModule = module {
    single<SharingResourcesProvider>{
        SharingResourcesProviderImpl(androidContext())
    }
}