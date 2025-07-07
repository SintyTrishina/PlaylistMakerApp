package com.example.playlistmakerapp.media.ui.di

import com.example.playlistmakerapp.media.ui.viewmodel.FavouritesViewModel
import com.example.playlistmakerapp.media.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaViewModelModule = module {
    viewModel {
        FavouritesViewModel(get())
    }
    viewModel { (playlistId: Int) ->
        PlaylistsViewModel(playlistId)
    }
}