package com.example.playlistmakerapp.main.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmakerapp.main.domain.api.InternalNavigation
import com.example.playlistmakerapp.util.Creator

class MainViewModel(private val internalNavigation: InternalNavigation) : ViewModel() {

    companion object {

        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {

                MainViewModel(
                    Creator.provideInternalNavigator(context)
                )
            }
        }
    }

    fun startSearch() {
        internalNavigation.startSearch()
    }

    fun startMedia() {
        internalNavigation.startMedia()
    }

    fun startSettings() {
        internalNavigation.startSettings()
    }

}