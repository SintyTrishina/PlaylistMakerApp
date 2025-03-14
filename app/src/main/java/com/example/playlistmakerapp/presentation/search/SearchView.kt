package com.example.playlistmakerapp.presentation.search

import com.example.playlistmakerapp.domain.models.Track
import com.example.playlistmakerapp.ui.search.models.SearchState
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

interface SearchView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun render(state: SearchState)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun navigateToAudioPlayer(track: Track)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showToast(message: String)
}









