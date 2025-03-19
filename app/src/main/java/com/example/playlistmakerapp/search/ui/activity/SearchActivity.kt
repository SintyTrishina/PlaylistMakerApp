package com.example.playlistmakerapp.search.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.databinding.ActivitySearchBinding
import com.example.playlistmakerapp.player.ui.AudioPlayerActivity
import com.example.playlistmakerapp.search.domain.models.Track
import com.example.playlistmakerapp.search.ui.SearchState
import com.example.playlistmakerapp.search.ui.TrackAdapter
import com.example.playlistmakerapp.search.ui.viewmodel.SearchViewModel
import com.example.playlistmakerapp.util.Constants

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private lateinit var viewModel: SearchViewModel


    private lateinit var binding: ActivitySearchBinding
    private var userText = ""

    private val trackAdapter = TrackAdapter {
        if (clickDebounce()) {
            viewModel.onTrackClicked(it)
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private var textWatcher: TextWatcher? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.trackRecyclerView.adapter = trackAdapter
        viewModel = ViewModelProvider(this, SearchViewModel.getViewModelFactory())[SearchViewModel::class.java]

        setupListeners()
        viewModel.searchState.observe(this) {
            render(it)
        }
        viewModel.tracks.observe(this) {
            // Обновляем UI с новым списком треков
            showContent(it)
        }
        viewModel.toastState.observe(this) {
            showToast(it)
        }
        viewModel.navigateToPlayer.observe(this) { track ->
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
        viewModel.onCreate()

    }

    private fun setupListeners() {

        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onSearchFocusChanged(hasFocus)
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.clearIcon.setOnClickListener {
            viewModel.onClearButtonClicked()
            binding.inputEditText.setText("")
            binding.clearIcon.visibility = View.GONE
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
        }

        binding.searchHistoryCleanButton.setOnClickListener {
            viewModel.onCleanHistoryClicked()
        }
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                viewModel.searchDebounce(changedText = s?.toString() ?: "")
                if (binding.inputEditText.hasFocus() && s.isNullOrEmpty()) {
                    viewModel.showSearchHistory()
                } else {
                    viewModel.hideSearchHistory()
                }

                if (s.isNullOrEmpty()) {
                    binding.placeholderImage.visibility = View.GONE
                    binding.placeholderMessage.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                userText = s.toString()
            }
        }
        textWatcher?.let { binding.inputEditText.addTextChangedListener(it) }

        binding.updateButton.setOnClickListener {
            viewModel.onUpdateButtonClicked()
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


    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { binding.inputEditText.removeTextChangedListener(it) }


    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.History -> showHistory(state.history)
            is SearchState.Error -> showError(state.errorMessage)
            is SearchState.Empty -> showEmpty(state.message)
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.placeholderImage.visibility = View.GONE
        binding.updateButton.visibility = View.GONE
        binding.searchHint.visibility = View.GONE
        binding.searchHistoryCleanButton.visibility = View.GONE
        binding.placeholderMessage.visibility = View.GONE
    }

    private fun showError(errorMessage: String) {
        binding.progressBar.visibility = View.GONE
        binding.placeholderImage.setImageResource(R.drawable.errorconnection)
        binding.placeholderImage.visibility = View.VISIBLE
        binding.updateButton.visibility = View.VISIBLE
        binding.searchHint.visibility = View.GONE
        binding.searchHistoryCleanButton.visibility = View.GONE
        binding.placeholderMessage.visibility = View.VISIBLE
        binding.placeholderMessage.text = errorMessage

    }

    private fun showEmpty(emptyMessage: String) {
        binding.progressBar.visibility = View.GONE
        binding.placeholderImage.setImageResource(R.drawable.error)
        binding.placeholderImage.visibility = View.VISIBLE
        binding.updateButton.visibility = View.GONE
        binding.searchHint.visibility = View.GONE
        binding.searchHistoryCleanButton.visibility = View.GONE
        binding.placeholderMessage.visibility = View.VISIBLE
        binding.placeholderMessage.text = emptyMessage
    }

    private fun showContent(tracks: List<Track>) {
        binding.progressBar.visibility = View.GONE
        binding.placeholderImage.visibility = View.GONE
        binding.updateButton.visibility = View.GONE
        binding.searchHint.visibility = View.GONE
        binding.searchHistoryCleanButton.visibility = View.GONE
        binding.placeholderMessage.visibility = View.GONE
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()

    }

    private fun showHistory(history: List<Track>) {
        binding.progressBar.visibility = View.GONE
        binding.placeholderImage.visibility = View.GONE
        binding.updateButton.visibility = View.GONE
        binding.searchHint.visibility = View.VISIBLE
        binding.searchHistoryCleanButton.visibility = View.VISIBLE
        binding.placeholderMessage.visibility = View.GONE
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(history)
        trackAdapter.notifyDataSetChanged()

    }
}

