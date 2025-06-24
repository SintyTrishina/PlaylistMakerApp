package com.example.playlistmakerapp.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmakerapp.search.domain.ResourcesProvider
import com.example.playlistmakerapp.search.domain.api.SearchHistoryInteractor
import com.example.playlistmakerapp.search.domain.api.TrackInteractor
import com.example.playlistmakerapp.search.domain.models.Track
import com.example.playlistmakerapp.search.ui.SearchState
import com.example.playlistmakerapp.search.ui.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val trackInteractor: TrackInteractor,
    private val resourcesProvider: ResourcesProvider
) : ViewModel() {

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> get() = _searchState

    private val _toastState = SingleLiveEvent<String?>()
    val toastState: LiveData<String?> get() = _toastState


    private val _navigateToPlayer = SingleLiveEvent<Track>()
    val navigateToPlayer: LiveData<Track> get() = _navigateToPlayer

    private var lastSearchText: String? = null

    private var searchJob: Job? = null

    fun onCreate() {
        searchHistoryInteractor.loadHistoryFromPrefs()
        showSearchHistory()
    }

    // Обработка нажатия на трек
    fun onTrackClicked(track: Track) {
        searchHistoryInteractor.addTrack(track)
        _navigateToPlayer.postValue(track)
    }

    // Обработка изменения фокуса поиска
    fun onSearchFocusChanged(hasFocus: Boolean) {
        if (hasFocus && lastSearchText?.isEmpty() == true) {
            showSearchHistory()
        } else {
            hideSearchHistory()
        }
    }

    // Обработка нажатия на кнопку очистки поиска
    fun onClearButtonClicked() {
        renderState(SearchState.Content(emptyList()))
        showSearchHistory()
    }

    // Обработка нажатия на кнопку очистки истории
    fun onCleanHistoryClicked() {
        searchHistoryInteractor.cleanHistory()
        hideSearchHistory()
    }

    // Обработка нажатия на кнопку обновления
    fun onUpdateButtonClicked() {
        lastSearchText?.let { search(it) }
    }

    fun searchDebounce(changedText: String) {
        if (lastSearchText == changedText) {
            return
        }
        lastSearchText = changedText
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            search(changedText)
        }
    }


    fun search(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)

            trackInteractor.search(
                newSearchText,
                object : TrackInteractor.TrackConsumer {
                    override fun consume(data: List<Track>?, errorMessage: String?) {

                        renderState(SearchState.Content(emptyList()))

                        if (!data.isNullOrEmpty()) {
                            renderState(SearchState.Content(data))
                        } else {
                            renderState(SearchState.Empty(resourcesProvider.getNothingFoundText()))
                        }
                        if (errorMessage != null) {
                            renderState(SearchState.Error(resourcesProvider.getSomethingWentWrongText()))
                            _toastState.postValue(errorMessage)

                        }
                    }
                })
        }
    }

    private fun renderState(state: SearchState) {
        _searchState.postValue(state)
    }


    fun showSearchHistory() {
        val history = searchHistoryInteractor.getHistory()
        if (history.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.Main) {
                renderState(SearchState.History(history))
            }
        } else {
            hideSearchHistory()
        }
    }

    fun hideSearchHistory() {
        viewModelScope.launch(Dispatchers.Main) {
            renderState(SearchState.Content(ArrayList()))
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}