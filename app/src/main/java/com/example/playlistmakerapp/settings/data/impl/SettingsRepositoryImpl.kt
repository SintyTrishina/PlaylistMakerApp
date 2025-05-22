package com.example.playlistmakerapp.settings.data.impl

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmakerapp.settings.domain.api.SettingsRepository
import com.example.playlistmakerapp.util.Constants

class SettingsRepositoryImpl(
    private val darkThemeSharedPrefs: SharedPreferences,
    private val context: Context
) : SettingsRepository {

    override fun switchTheme(isDarkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        darkThemeSharedPrefs.edit().putBoolean(Constants.SWITCH_KEY, isDarkTheme).apply()
    }

    override fun getTheme(): Boolean {
        // Если значение есть в SharedPreferences - возвращаем его
        if (darkThemeSharedPrefs.contains(Constants.SWITCH_KEY)) {
            return darkThemeSharedPrefs.getBoolean(Constants.SWITCH_KEY, false)
        }

        // Если значения нет - определяем системную тему и сохраняем
        val isSystemDark = isSystemInDarkTheme()
        darkThemeSharedPrefs.edit().putBoolean(Constants.SWITCH_KEY, isSystemDark).apply()
        return isSystemDark
    }

    private fun isSystemInDarkTheme(): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

}
