package com.example.playlistmakerapp.ui.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
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
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.domain.models.Track
import com.example.playlistmakerapp.presentation.search.SearchPresenter
import com.example.playlistmakerapp.presentation.search.SearchView
import com.example.playlistmakerapp.ui.Constants
import com.example.playlistmakerapp.ui.app.App
import com.example.playlistmakerapp.ui.audio_player.AudioPlayerActivity
import com.example.playlistmakerapp.ui.search.models.SearchState
import com.example.playlistmakerapp.util.Creator

class SearchActivity : AppCompatActivity(), SearchView {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L

    }

    private var searchPresenter: SearchPresenter? = null
    private var userText = ""
    private lateinit var inputEditText: EditText
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var updateButton: Button
    private lateinit var cleanSearchButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var textSearch: TextView
    private lateinit var clearButton: ImageView

    private val trackAdapter = TrackAdapter {
        if (clickDebounce()) {
            searchPresenter?.onTrackClicked(it)
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private var textWatcher: TextWatcher? = null

    override fun onStart() {
        super.onStart()
        searchPresenter?.attachView(this)
    }

    override fun onResume() {
        super.onResume()
        searchPresenter?.attachView(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initViews()
        setupListeners()

        searchPresenter = (this.applicationContext as? App)?.searchPresenter

        if (searchPresenter == null) {
            searchPresenter = Creator.provideSearchPresenter(
                this.applicationContext
            )
            (this.applicationContext as? App)?.searchPresenter = searchPresenter
        }
        searchPresenter?.onCreate()

    }

    private fun initViews() {
        inputEditText = findViewById(R.id.inputEditText)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderImage = findViewById(R.id.placeholderImage)
        updateButton = findViewById(R.id.updateButton)
        cleanSearchButton = findViewById(R.id.searchHistoryCleanButton)
        textSearch = findViewById(R.id.searchHint)
        progressBar = findViewById(R.id.progressBar)
        clearButton = findViewById(R.id.clearIcon)

        val trackRecyclerView = findViewById<RecyclerView>(R.id.trackRecyclerView)
        trackRecyclerView.adapter = trackAdapter
    }

    private fun setupListeners() {

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            searchPresenter?.onSearchFocusChanged(hasFocus)
        }

        findViewById<ImageView>(R.id.back).setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            searchPresenter?.onClearButtonClicked()
//            tracks.clear()
            inputEditText.setText("")
//            searchHistoryInteractor.loadHistoryFromPrefs()
//            searchPresenter?.showSearchHistory()
//            searchPresenter?.showMessage("", "")
//
            clearButton.visibility = View.GONE
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        cleanSearchButton.setOnClickListener {
            searchPresenter?.onCleanHistoryClicked()
//            searchHistoryInteractor.cleanHistory()
//            searchPresenter?.hideSearchHistory()
        }
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                clearButton.isVisible = !s.isNullOrEmpty()
                searchPresenter?.searchDebounce(changedText = s?.toString() ?: "")
                if (inputEditText.hasFocus() && s.isNullOrEmpty()) {
                    searchPresenter?.showSearchHistory()
                } else {
                    searchPresenter?.hideSearchHistory()
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
        textWatcher?.let { inputEditText.addTextChangedListener(it) }

        updateButton.setOnClickListener {
            searchPresenter?.onUpdateButtonClicked()
//            searchPresenter?.search()
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

    override fun onPause() {
        super.onPause()
        searchPresenter?.detachView()
    }

    override fun onStop() {
        super.onStop()
        searchPresenter?.detachView()
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { inputEditText.removeTextChangedListener(it) }
        searchPresenter?.detachView()
        searchPresenter?.onDestroy()

        if (isFinishing()) {
            // Очищаем ссылку на Presenter в Application
            (this.application as? App)?.searchPresenter = null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        searchPresenter?.onSaveInstanceState(outState)
        searchPresenter?.detachView()
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchPresenter?.onRestoreInstanceState(savedInstanceState)
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.History -> showHistory(state.history)
            is SearchState.Error -> showError(state.errorMessage)
            is SearchState.Empty -> showEmpty(state.message)
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        placeholderImage.visibility = View.GONE
        updateButton.visibility = View.GONE
        textSearch.visibility = View.GONE
        cleanSearchButton.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
    }

    private fun showError(errorMessage: String) {
        progressBar.visibility = View.GONE
        placeholderImage.setImageResource(R.drawable.errorconnection)
        placeholderImage.visibility = View.VISIBLE
        updateButton.visibility = View.VISIBLE
        textSearch.visibility = View.GONE
        cleanSearchButton.visibility = View.GONE
        placeholderMessage.visibility = View.VISIBLE
        placeholderMessage.text = errorMessage

    }

    private fun showEmpty(emptyMessage: String) {
        progressBar.visibility = View.GONE
        placeholderImage.setImageResource(R.drawable.error)
        placeholderImage.visibility = View.VISIBLE
        updateButton.visibility = View.GONE
        textSearch.visibility = View.GONE
        cleanSearchButton.visibility = View.GONE
        placeholderMessage.visibility = View.VISIBLE
        placeholderMessage.text = emptyMessage
    }

    private fun showContent(tracks: List<Track>) {
        progressBar.visibility = View.GONE
        placeholderImage.visibility = View.GONE
        updateButton.visibility = View.GONE
        textSearch.visibility = View.GONE
        cleanSearchButton.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()

    }

    private fun showHistory(history: List<Track>) {
        progressBar.visibility = View.GONE
        placeholderImage.visibility = View.GONE
        updateButton.visibility = View.GONE
        textSearch.visibility = View.VISIBLE
        cleanSearchButton.visibility = View.VISIBLE
        placeholderMessage.visibility = View.GONE
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(history)
        trackAdapter.notifyDataSetChanged()

    }

    override fun navigateToAudioPlayer(track: Track) {
        val intent = Intent(this, AudioPlayerActivity::class.java).apply {
            putExtra(Constants.TRACK_ID, track.trackId)
            putExtra(Constants.TRACK_NAME, track.trackName)
            putExtra(Constants.ARTIST_NAME, track.artistName)
            putExtra(Constants.COLLECTION_NAME, track.collectionName)
            putExtra(Constants.RELEASE_DATE, track.releaseDate)
            putExtra(Constants.PRIMARY_GENRE_NAME, track.primaryGenreName)
            putExtra(Constants.COUNTRY, track.country)
            putExtra(Constants.TRACK_TIME_MILLIS, track.trackTimeMillis)
            putExtra(Constants.ART_WORK_URL, track.artworkUrl100)
            putExtra(Constants.PREVIEW_URL, track.previewUrl)
        }
        startActivity(intent)
    }
}

