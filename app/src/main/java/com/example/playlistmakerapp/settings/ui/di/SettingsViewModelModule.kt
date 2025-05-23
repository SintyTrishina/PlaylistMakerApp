package com.example.playlistmakerapp.settings.ui.di

import android.app.Activity
import com.example.playlistmakerapp.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsViewModelModule = module {
    viewModel { (activity: Activity) ->
        SettingsViewModel(
            settingsInteractor = get(),
            sharingInteractor = get()
        )
    }
}