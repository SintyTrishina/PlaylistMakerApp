package com.example.playlistmakerapp.main.ui.di

import android.app.Activity
import com.example.playlistmakerapp.main.ui.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val mainViewModelModule = module {
    viewModel { (activity: Activity) ->
        MainViewModel(get { parametersOf(activity) })
    }
}