package com.example.playlistmakerapp.settings.data.di

import com.example.playlistmakerapp.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmakerapp.settings.domain.api.SettingsRepository
import org.koin.dsl.module

val settingsRepositoryModule = module {

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }
}