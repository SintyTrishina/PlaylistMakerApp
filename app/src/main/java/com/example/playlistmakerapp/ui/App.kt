package com.example.playlistmakerapp.ui

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmakerapp.Constants

class App : Application() {

    private lateinit var sharedPref: SharedPreferences
    private var darkThemePrivate = false
    val darkTheme:Boolean
        get() = darkThemePrivate

    override fun onCreate() {
        super.onCreate()
        sharedPref = getSharedPreferences(Constants.THEME_SETTINGS, MODE_PRIVATE)
        darkThemePrivate = sharedPref.getBoolean(Constants.SWITCH_KEY, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkThemePrivate = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPref.edit()
            .putBoolean(Constants.SWITCH_KEY, darkThemeEnabled)
            .apply()
    }
}
