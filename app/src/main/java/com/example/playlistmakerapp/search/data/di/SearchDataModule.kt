package com.example.playlistmakerapp.search.data.di

import android.content.Context
import androidx.room.Room
import com.example.playlistmakerapp.search.data.NetworkClient
import com.example.playlistmakerapp.search.data.ResourcesProviderImpl
import com.example.playlistmakerapp.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmakerapp.search.data.TrackDbConvertor
import com.example.playlistmakerapp.search.data.db.AppDataBase
import com.example.playlistmakerapp.search.data.network.RetrofitNetworkClient
import com.example.playlistmakerapp.search.data.network.TrackApi
import com.example.playlistmakerapp.search.domain.ResourcesProvider
import com.example.playlistmakerapp.search.domain.api.SearchHistoryRepository
import com.example.playlistmakerapp.util.Constants
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchDataModule = module {

    single<TrackApi> {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackApi::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE)
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

    single {
        Room.databaseBuilder(androidContext(), AppDataBase::class.java, "database.db")
            .fallbackToDestructiveMigration(false)
            .build()
    }

    factory { TrackDbConvertor() }

}