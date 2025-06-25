package com.example.playlistmakerapp.search.ui.di

import com.example.playlistmakerapp.search.ui.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val searchViewModelModule = module {

    viewModel {
        SearchViewModel(get(), get(), get(), get())
    }
}