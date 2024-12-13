package com.example.playlistmakerapp

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

const val FILE_PREFERENCES = "file_preferences"
const val SWITCH_KEY = "switch_key"

class App : Application() {

    private lateinit var sharedPref: SharedPreferences
    private var darkThemePrivate = false
    val darkTheme:Boolean
        get() = darkThemePrivate

    override fun onCreate() {
        super.onCreate()
        sharedPref = getSharedPreferences(FILE_PREFERENCES, MODE_PRIVATE)
        darkThemePrivate = sharedPref.getBoolean(SWITCH_KEY, false)
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
            .putBoolean(SWITCH_KEY, darkThemeEnabled)
            .apply()


    }
}
