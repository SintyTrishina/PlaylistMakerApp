package com.example.playlistmakerapp.settings.domain.api

interface SettingsInteractor {
    fun switchTheme(isDarkTheme: Boolean)
    fun getTheme(): Boolean
}