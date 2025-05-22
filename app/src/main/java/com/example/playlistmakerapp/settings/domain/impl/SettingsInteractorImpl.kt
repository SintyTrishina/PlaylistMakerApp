package com.example.playlistmakerapp.settings.domain.impl

import com.example.playlistmakerapp.settings.domain.api.SettingsInteractor
import com.example.playlistmakerapp.settings.domain.api.SettingsRepository

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository) :
    SettingsInteractor {
    override fun switchTheme(isDarkTheme: Boolean) {
        settingsRepository.switchTheme(isDarkTheme)
    }

    override fun getTheme(): Boolean {
        return settingsRepository.getTheme()
    }
}