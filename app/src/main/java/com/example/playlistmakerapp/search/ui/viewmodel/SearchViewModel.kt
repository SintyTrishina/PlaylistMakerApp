package com.example.playlistmakerapp.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val trackInteractor: TrackInteractor,
    private val resourcesProvider: ResourcesProvider,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> get() = _searchState

    private val _toastState = SingleLiveEvent<String?>()
    val toastState: LiveData<String?> get() = _toastState

    private val _navigateToPlayer = SingleLiveEvent<Track>()
    val navigateToPlayer: LiveData<Track> get() = _navigateToPlayer

    private var searchJob: Job? = null

    var currentTracks: List<Track>?
        get() = savedStateHandle.get("current_tracks")
        set(value) {
            savedStateHandle["current_tracks"] = value
        }

    var lastSearchText: String?
        get() = savedStateHandle.get("last_search_text")
        set(value) {
            savedStateHandle["last_search_text"] = value
        }

    init {
        loadInitialState()
    }

    private fun loadInitialState() {
        searchHistoryInteractor.loadHistoryFromPrefs()
        when {
            !lastSearchText.isNullOrEmpty() -> lastSearchText?.let { search(it) }
            searchHistoryInteractor.getHistory().isNotEmpty() -> showSearchHistory()
            else -> renderState(SearchState.Content(emptyList()))
        }
    }

    fun onTrackClicked(track: Track) {
        searchHistoryInteractor.addTrack(track)
        _navigateToPlayer.postValue(track)
    }

    fun onSearchFocusChanged(hasFocus: Boolean) {
        if (hasFocus && lastSearchText.isNullOrEmpty()) {
            showSearchHistory()
        }
    }

    fun onClearButtonClicked() {
        lastSearchText = ""
        renderState(SearchState.Content(emptyList()))
        showSearchHistory()
    }

    fun onCleanHistoryClicked() {
        searchHistoryInteractor.cleanHistory()
        renderState(SearchState.Content(emptyList()))
    }

    fun onUpdateButtonClicked() {
        lastSearchText?.let { search(it) }
    }

    fun updateSearchText(text: String) {
        lastSearchText = text
    }

    fun searchDebounce(changedText: String) {
        if (lastSearchText == changedText) return

        lastSearchText = changedText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            search(changedText)
        }
    }

    private fun search(newSearchText: String) {
        if (newSearchText.isEmpty()) return

        viewModelScope.launch {
            // Устанавливаем состояние загрузки в основном потоке
            _searchState.postValue(SearchState.Loading)

            try {
                trackInteractor.search(newSearchText).collect { (tracks, error) ->
                    // Обрабатываем результат в IO-потоке
                    withContext(Dispatchers.IO) {
                        processSearchResult(tracks, error)
                    }
                }
            } catch (e: Exception) {
                // Обработка ошибок при выполнении запроса
                _toastState.postValue(resourcesProvider.getSomethingWentWrongText())
                _searchState.postValue(SearchState.Error(resourcesProvider.getSomethingWentWrongText()))
            }
        }
    }

    private fun processSearchResult(tracks: List<Track>?, error: String?) {
        when {
            error != null -> {
                _toastState.postValue(error)
                _searchState.postValue(SearchState.Error(resourcesProvider.getSomethingWentWrongText()))
            }

            tracks.isNullOrEmpty() -> {
                _searchState.postValue(SearchState.Empty(resourcesProvider.getNothingFoundText()))
            }

            else -> {
                currentTracks = tracks
                _searchState.postValue(SearchState.Content(tracks))
            }
        }
    }

    fun showSearchHistory() {
        val history = searchHistoryInteractor.getHistory()
        if (history.isNotEmpty()) {
            currentTracks = history
            renderState(SearchState.History(history))
        } else {
            renderState(SearchState.Content(emptyList()))
        }
    }

    private fun renderState(state: SearchState) {
        _searchState.postValue(state)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}