package com.example.playlistmakerapp.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmakerapp.data.SearchHistoryRepositoryImpl
import com.example.playlistmakerapp.data.TrackRepositoryImpl
import com.example.playlistmakerapp.data.network.RetrofitNetworkClient
import com.example.playlistmakerapp.domain.api.SearchHistoryInteractor
import com.example.playlistmakerapp.domain.api.SearchHistoryRepository
import com.example.playlistmakerapp.domain.api.TrackInteractor
import com.example.playlistmakerapp.domain.api.TrackRepository
import com.example.playlistmakerapp.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmakerapp.domain.impl.TrackInteractorImpl
import com.example.playlistmakerapp.presentation.search.SearchPresenter
import com.example.playlistmakerapp.presentation.search.SearchView
import com.example.playlistmakerapp.ui.search.TrackAdapter

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

    fun provideSearchPresenter(
        context: Context
    ): SearchPresenter {
        return SearchPresenter(context)
    }
}