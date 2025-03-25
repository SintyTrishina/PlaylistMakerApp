package com.example.playlistmakerapp.search.data.di

import android.content.Context
import com.example.playlistmakerapp.search.data.NetworkClient
import com.example.playlistmakerapp.search.data.ResourcesProviderImpl
import com.example.playlistmakerapp.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmakerapp.search.data.network.RetrofitNetworkClient
import com.example.playlistmakerapp.search.data.network.TrackApi
import com.example.playlistmakerapp.search.domain.ResourcesProvider
import com.example.playlistmakerapp.search.domain.api.SearchHistoryRepository
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchDataModule = module {

    single<TrackApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackApi::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences("search_history", Context.MODE_PRIVATE)
    }

    single<ResourcesProvider> {
        ResourcesProviderImpl(androidContext())
    }

    factory { Gson() }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(androidContext(), get())
    }

}