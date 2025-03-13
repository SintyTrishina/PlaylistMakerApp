package com.example.playlistmakerapp.presentation.search

import com.example.playlistmakerapp.domain.models.Track
import com.example.playlistmakerapp.ui.search.models.SearchState

interface SearchView {

    fun render(state: SearchState)

    fun navigateToAudioPlayer(track: Track)

    fun showToast(message: String)
}









