package com.example.playlistmakerapp.util

import android.app.Application
import com.example.playlistmakerapp.main.domain.api.di.internalNavigationModule
import com.example.playlistmakerapp.main.ui.di.mainViewModelModule
import com.example.playlistmakerapp.player.ui.di.playerViewModelModule
import com.example.playlistmakerapp.search.data.di.searchDataModule
import com.example.playlistmakerapp.search.domain.di.interactorModule
import com.example.playlistmakerapp.search.domain.di.trackRepositoryModule
import com.example.playlistmakerapp.search.ui.di.searchViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {


    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                searchDataModule,
                searchViewModelModule,
                trackRepositoryModule,
                interactorModule,
                internalNavigationModule,
                mainViewModelModule,
                playerViewModelModule
            )
        }
        Creator.initialize(this)
    }
}
