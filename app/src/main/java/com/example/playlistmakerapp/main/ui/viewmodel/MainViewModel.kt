package com.example.playlistmakerapp.main.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.playlistmakerapp.main.domain.api.InternalNavigation

class MainViewModel(private val internalNavigation: InternalNavigation) : ViewModel() {

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