package com.example.playlistmakerapp.settings.domain.api

interface SettingsRepository {
    fun switchTheme(isDarkTheme: Boolean)
    fun isDarkThemeEnabled(): Boolean
}