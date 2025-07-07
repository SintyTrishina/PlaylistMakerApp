package com.example.playlistmakerapp.player.ui.di

import com.example.playlistmakerapp.player.ui.viewmodel.AudioPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerViewModelModule = module {
    viewModel {
        AudioPlayerViewModel(get(), get())
    }
}