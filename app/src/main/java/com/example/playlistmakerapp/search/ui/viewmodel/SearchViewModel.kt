package com.example.playlistmakerapp.search.ui.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.search.domain.api.TrackInteractor
import com.example.playlistmakerapp.search.domain.models.Track
import com.example.playlistmakerapp.search.ui.SearchState
import com.example.playlistmakerapp.search.ui.SingleLiveEvent
import com.example.playlistmakerapp.util.Creator

class SearchViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> get() = _searchState

    private val _toastState = SingleLiveEvent<String>()
    val toastState: LiveData<String> get() = _toastState


    private val _navigateToPlayer = SingleLiveEvent<Track>()
    val navigateToPlayer: LiveData<Track> get() = _navigateToPlayer

    private var tracks = ArrayList<Track>()
    private var lastSearchText: String? = null

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val USER_TEXT = "USER_TEXT"
        private const val TRACK_LIST_KEY = "TRACK_LIST_KEY"

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }


    private val searchHistoryInteractor =
        Creator.provideSearchHistoryInteractor(getApplication())
    private val trackInteractor = Creator.provideTrackInteractor(getApplication())

    private val handler = Handler(Looper.getMainLooper())


    private val searchRunnable = Runnable {
        val newSearchText = lastSearchText ?: ""
        search(newSearchText)
    }

    fun onCreate() {
        searchHistoryInteractor.loadHistoryFromPrefs()
        showSearchHistory()
    }

    override fun onCleared() {
        handler.removeCallbacks(searchRunnable)
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
        renderState(SearchState.Content(tracks))
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

        this.lastSearchText = changedText
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }


    fun search(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)

            trackInteractor.search(
                newSearchText,
                object : TrackInteractor.TrackConsumer {
                    override fun consume(data: List<Track>?, errorMessage: String?) {

                        tracks.clear()

                        if (!data.isNullOrEmpty()) {
                            tracks.addAll(data)
                            renderState(SearchState.Content(tracks))
                        } else {
                            renderState(SearchState.Empty(getApplication<Application>().getString(R.string.nothing_found)))
                        }
                        if (errorMessage != null) {
                            renderState(SearchState.Error(getApplication<Application>().getString(R.string.something_went_wrong)))
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
        handler.post {
            val history = searchHistoryInteractor.getHistory()
            if (history.isNotEmpty()) {
                renderState(SearchState.History(history))
            } else {
                hideSearchHistory()
            }
        }
    }

    fun hideSearchHistory() {
        handler.post {
            renderState(SearchState.Content(ArrayList()))
        }
    }


//    fun onSaveInstanceState(outState: Bundle) {
//        outState.putString(USER_TEXT, lastSearchText)
//
//        val json = Gson().toJson(tracks)
//        outState.putString(TRACK_LIST_KEY, json)
//
//        searchHistoryInteractor.getHistory()
//    }
//
//    fun onRestoreInstanceState(savedInstanceState: Bundle) {
//
//        lastSearchText = savedInstanceState.getString(USER_TEXT, "")
//
//        val json = savedInstanceState.getString(TRACK_LIST_KEY, "[]")
//        val type = object : TypeToken<ArrayList<Track>>() {}.type
//        tracks = Gson().fromJson(json, type)
//
//        if (tracks.isNotEmpty()) {
//            _searchState.postValue(SearchState.Content(tracks))
//        } else {
//            showSearchHistory()
//        }
//
//        searchHistoryInteractor.loadHistoryFromPrefs()
//    }

}