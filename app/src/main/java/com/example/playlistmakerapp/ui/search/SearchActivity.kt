package com.example.playlistmakerapp.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmakerapp.Constants
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.creator.Creator
import com.example.playlistmakerapp.domain.api.SearchHistoryInteractor
import com.example.playlistmakerapp.domain.api.TrackInteractor
import com.example.playlistmakerapp.domain.models.Track
import com.example.playlistmakerapp.ui.audio_player.AudioPlayerActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchActivity : AppCompatActivity() {

    private var userText: String = ""
    private lateinit var inputEditText: EditText
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var updateButton: Button
    private lateinit var cleanSearchButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var textSearch: TextView
    private var tracks = ArrayList<Track>()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor
    private val trackInteractor = Creator.provideTrackInteractor()

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        inputEditText = findViewById(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val buttonBack = findViewById<ImageView>(R.id.back)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderImage = findViewById(R.id.placeholderImage)
        updateButton = findViewById(R.id.updateButton)
        val trackRecyclerView = findViewById<RecyclerView>(R.id.trackRecyclerView)
        cleanSearchButton = findViewById(R.id.searchHistoryCleanButton)
        textSearch = findViewById(R.id.searchHint)
        progressBar = findViewById(R.id.progressBar)

        searchHistoryInteractor = Creator.provideSearchHistoryInteractor(this)

        trackAdapter = TrackAdapter {
            if (clickDebounce()) {
                searchHistoryInteractor.addTrack(it)
                val intentAudioPlayerActivity =
                    Intent(this, AudioPlayerActivity::class.java).apply {
                        putExtra(Constants.TRACK_ID, it.trackId)
                        putExtra(Constants.TRACK_NAME, it.trackName)
                        putExtra(Constants.ARTIST_NAME, it.artistName)
                        putExtra(Constants.COLLECTION_NAME, it.collectionName)
                        putExtra(Constants.RELEASE_DATE, it.releaseDate)
                        putExtra(Constants.PRIMARY_GENRE_NAME, it.primaryGenreName)
                        putExtra(Constants.COUNTRY, it.country)
                        putExtra(Constants.TRACK_TIME_MILLIS, it.trackTimeMillis)
                        putExtra(Constants.ART_WORK_URL, it.artworkUrl100)
                        putExtra(Constants.PREVIEW_URL, it.previewUrl)
                    }
                startActivity(intentAudioPlayerActivity)
            }
        }

        trackRecyclerView.adapter = trackAdapter
        searchHistoryInteractor.loadHistoryFromPrefs()

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                showSearchHistory()
            } else {
                hideSearchHistory()
            }
        }

        buttonBack.setOnClickListener {
            finish()
        }

        cleanSearchButton.setOnClickListener {
            searchHistoryInteractor.cleanHistory()
            hideSearchHistory()
        }

        clearButton.setOnClickListener {
            tracks.clear()
            inputEditText.setText("")
            searchHistoryInteractor.loadHistoryFromPrefs()
            showSearchHistory()
            showMessage("", "")

            clearButton.visibility = View.GONE
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                searchDebounce()
                if (inputEditText.hasFocus() && s.isNullOrEmpty()) {
                    showSearchHistory()
                } else {
                    hideSearchHistory()
                }

                if (s.isNullOrEmpty()) {
                    placeholderImage.visibility = View.GONE
                    placeholderMessage.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                userText = s.toString()
            }
        }

        inputEditText.addTextChangedListener(textWatcher)


        updateButton.setOnClickListener {
            search()
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private val searchRunnable = Runnable { search() }

    private fun search() {
        if (inputEditText.text.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE

            trackInteractor.search(
                inputEditText.text.toString(),
                object : TrackInteractor.TrackConsumer {
                    override fun consume(data: List<Track>?) {
                        handler.post {
                            progressBar.visibility = View.GONE
                            tracks.clear()

                            if (!data.isNullOrEmpty()) {
                                tracks.addAll(data)
                                showTracks()
                                updateUI(isEmpty = false, isError = false)
                            } else {
                                updateUI(isEmpty = true, isError = false)
                            }
                        }
                    }

                    override fun onError(error: Throwable) {
                        handler.post {
                            progressBar.visibility = View.GONE
                            updateUI(isEmpty = false, isError = true)
                            Log.e("SearchActivity", "Error during search: ${error.message}")
                        }
                    }
                })
        }
    }

    private fun updateUI(isEmpty: Boolean, isError: Boolean) {
        handler.post {
            if (isEmpty) {
                placeholderImage.setImageResource(R.drawable.error)
                placeholderImage.visibility = View.VISIBLE
                updateButton.visibility = View.GONE
                textSearch.visibility = View.GONE
                cleanSearchButton.visibility = View.GONE
                showMessage(getString(R.string.nothing_found), "")
            } else if (isError) {
                placeholderImage.setImageResource(R.drawable.errorconnection)
                placeholderImage.visibility = View.VISIBLE
                updateButton.visibility = View.VISIBLE
                textSearch.visibility = View.GONE
                cleanSearchButton.visibility = View.GONE
                showMessage(getString(R.string.something_went_wrong), "")
            } else {
                placeholderImage.visibility = View.GONE
                updateButton.visibility = View.GONE
                textSearch.visibility = View.GONE
                cleanSearchButton.visibility = View.GONE
                showMessage("", "")
            }
        }
    }


    private fun showMessage(text: String, additionalMessage: String) {
        handler.post {
            if (text.isNotEmpty()) {
                placeholderMessage.visibility = View.VISIBLE
                tracks.clear()
                trackAdapter.updateTracks(tracks)
                placeholderMessage.text = text
                if (additionalMessage.isNotEmpty()) {
                    Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG).show()
                }
            } else {
                placeholderMessage.visibility = View.GONE
            }
        }
    }

    private fun showSearchHistory() {
        handler.post {
            val history = searchHistoryInteractor.getHistory()
            if (history.isNotEmpty()) {
                textSearch.visibility = View.VISIBLE
                cleanSearchButton.visibility = View.VISIBLE
                placeholderImage.visibility = View.GONE
                updateButton.visibility = View.GONE
                trackAdapter.updateTracks(history)
            } else {
                hideSearchHistory()
            }
        }
    }

    private fun hideSearchHistory() {
        handler.post {
            textSearch.visibility = View.GONE
            cleanSearchButton.visibility = View.GONE
            placeholderImage.visibility = View.GONE
            updateButton.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
            trackAdapter.updateTracks(ArrayList())
        }
    }


    private fun showTracks() {
        handler.post {
            if (tracks.isNotEmpty()) {
                trackAdapter.updateTracks(tracks)
                textSearch.visibility = View.GONE
                cleanSearchButton.visibility = View.GONE
                placeholderImage.visibility = View.GONE
                placeholderMessage.visibility = View.GONE
                updateButton.visibility = View.GONE
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(USER_TEXT, userText)

        val json = Gson().toJson(tracks)
        outState.putString(TRACK_LIST_KEY, json)

        searchHistoryInteractor.getHistory()
    }

    companion object {
        private const val USER_TEXT = "USER_TEXT"
        private const val TRACK_LIST_KEY = "TRACK_LIST_KEY"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        userText = savedInstanceState.getString(USER_TEXT, "")
        inputEditText.setText(userText)

        val json = savedInstanceState.getString(TRACK_LIST_KEY, "[]")
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        tracks = Gson().fromJson(json, type)
        trackAdapter.updateTracks(tracks)

        searchHistoryInteractor.loadHistoryFromPrefs()
        showSearchHistory()
    }
}