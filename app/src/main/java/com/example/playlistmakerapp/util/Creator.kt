package com.example.playlistmakerapp.util

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmakerapp.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmakerapp.search.data.TrackRepositoryImpl
import com.example.playlistmakerapp.search.data.network.RetrofitNetworkClient
import com.example.playlistmakerapp.search.domain.api.SearchHistoryInteractor
import com.example.playlistmakerapp.search.domain.api.SearchHistoryRepository
import com.example.playlistmakerapp.search.domain.api.TrackInteractor
import com.example.playlistmakerapp.search.domain.api.TrackRepository
import com.example.playlistmakerapp.search.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmakerapp.search.domain.impl.TrackInteractorImpl
import com.example.playlistmakerapp.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmakerapp.settings.domain.api.SettingsRepository
import com.example.playlistmakerapp.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmakerapp.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmakerapp.sharing.domain.api.ExternalNavigator
import com.example.playlistmakerapp.sharing.domain.api.SharingInteractor
import com.example.playlistmakerapp.sharing.domain.impl.SharingInteractorImpl

object Creator {

    private const val SEARCH_HISTORY = "search_history"
    private const val MODE_PRIVATE = Context.MODE_PRIVATE

    private fun getTrackRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTrackInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(context))
    }

    private fun createSearchHistoryRepository(context: Context): SearchHistoryRepository {
        val sharedPrefs: SharedPreferences =
            context.getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE)
        return SearchHistoryRepositoryImpl(sharedPrefs)
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        val repository = createSearchHistoryRepository(context)
        return SearchHistoryInteractorImpl(repository)
    }
    private lateinit var applicationContext: Context

    fun initialize(context: Context) {
        applicationContext = context
    }

     fun provideSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(applicationContext)
    }

    private fun provideExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

     fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(provideExternalNavigator(context), context)
    }



}