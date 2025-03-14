package com.example.playlistmakerapp.presentation.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.domain.api.TrackInteractor
import com.example.playlistmakerapp.domain.models.Track
import com.example.playlistmakerapp.ui.search.models.SearchState
import com.example.playlistmakerapp.util.Creator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import moxy.MvpPresenter

class SearchPresenter(
    private val context: Context
): MvpPresenter<SearchView>() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val USER_TEXT = "USER_TEXT"
        private const val TRACK_LIST_KEY = "TRACK_LIST_KEY"
    }

    private var tracks = ArrayList<Track>()

    private val searchHistoryInteractor = Creator.provideSearchHistoryInteractor(context)
    private val trackInteractor = Creator.provideTrackInteractor(context)

    private val handler = Handler(Looper.getMainLooper())
    private var lastSearchText: String? = null

    private val searchRunnable = Runnable {
        val newSearchText = lastSearchText ?: ""
        search(newSearchText)
    }

    fun onCreate() {
        searchHistoryInteractor.loadHistoryFromPrefs()
        showSearchHistory()
    }

    override fun onDestroy() {
        handler.removeCallbacks(searchRunnable)
    }

    // Обработка нажатия на трек
    fun onTrackClicked(track: Track) {
        searchHistoryInteractor.addTrack(track)
        viewState.navigateToAudioPlayer(track)
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
                        handler.post {

                            tracks.clear()

                            if (!data.isNullOrEmpty()) {
                                tracks.addAll(data)
                                renderState(SearchState.Content(tracks))
                            } else {
                                renderState(SearchState.Empty(context.getString(R.string.nothing_found)))
                            }
                            if (errorMessage != null) {
                                renderState(SearchState.Error(context.getString(R.string.something_went_wrong)))
                                viewState.showToast(errorMessage)

                            }
                        }
                    }
                })
        }
    }

    private fun renderState(state: SearchState) {
        viewState.render(state)
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


    fun onSaveInstanceState(outState: Bundle) {
        outState.putString(USER_TEXT, lastSearchText)

        val json = Gson().toJson(tracks)
        outState.putString(TRACK_LIST_KEY, json)

        searchHistoryInteractor.getHistory()
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {

        lastSearchText = savedInstanceState.getString(USER_TEXT, "")

        val json = savedInstanceState.getString(TRACK_LIST_KEY, "[]")
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        tracks = Gson().fromJson(json, type)

        if (tracks.isNotEmpty()) {
            viewState.render(SearchState.Content(tracks))
        } else {
            showSearchHistory()
        }

        searchHistoryInteractor.loadHistoryFromPrefs()
    }

}