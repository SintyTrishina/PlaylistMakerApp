package com.example.playlistmakerapp.util

import android.app.Application
import com.example.playlistmakerapp.search.data.di.searchDataModule
import com.example.playlistmakerapp.search.domain.di.interactorModule
import com.example.playlistmakerapp.search.domain.di.trackRepositoryModule
import com.example.playlistmakerapp.search.ui.di.searchViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

//    private lateinit var sharedPref: SharedPreferences
//    private var darkThemePrivate = false
//    val darkTheme: Boolean
//        get() = darkThemePrivate

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(searchDataModule, searchViewModelModule, trackRepositoryModule, interactorModule)
        }
        Creator.initialize(this)
//        sharedPref = getSharedPreferences(Constants.THEME_SETTINGS, MODE_PRIVATE)
//        darkThemePrivate = sharedPref.getBoolean(Constants.SWITCH_KEY, false)
//        switchTheme(darkTheme)
    }
//
//    fun switchTheme(darkThemeEnabled: Boolean) {
////        darkThemePrivate = darkThemeEnabled
////        AppCompatDelegate.setDefaultNightMode(
////            if (darkThemeEnabled) {
////                AppCompatDelegate.MODE_NIGHT_YES
////            } else {
////                AppCompatDelegate.MODE_NIGHT_NO
////            }
////        )
////        sharedPref.edit()
////            .putBoolean(Constants.SWITCH_KEY, darkThemeEnabled)
////            .apply()
//    }
}
