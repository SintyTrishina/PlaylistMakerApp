package com.example.playlistmakerapp.settings.data.impl

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmakerapp.settings.domain.api.SettingsRepository
import com.example.playlistmakerapp.util.Constants

class SettingsRepositoryImpl(
    private val sharedPrefs: SharedPreferences
) : SettingsRepository {

    override fun switchTheme(isDarkTheme: Boolean) {
        sharedPrefs.edit().putBoolean(Constants.SWITCH_KEY, isDarkTheme).apply()
        applyTheme(isDarkTheme)
    }

    override fun isDarkThemeEnabled(): Boolean {
        return sharedPrefs.getBoolean(Constants.SWITCH_KEY, false)
    }

    private fun applyTheme(isDarkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
