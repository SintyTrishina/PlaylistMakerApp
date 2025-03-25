package com.example.playlistmakerapp.settings.domain.di

import com.example.playlistmakerapp.settings.domain.api.SettingsInteractor
import com.example.playlistmakerapp.settings.domain.impl.SettingsInteractorImpl
import org.koin.dsl.module

val settingsInteractorModule = module {
    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }
}