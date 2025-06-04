package com.example.playlistmakerapp.search.ui.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.databinding.FragmentSearchBinding
import com.example.playlistmakerapp.player.ui.fragment.AudioPlayerFragment
import com.example.playlistmakerapp.search.domain.models.Track
import com.example.playlistmakerapp.search.ui.SearchState
import com.example.playlistmakerapp.search.ui.TrackAdapter
import com.example.playlistmakerapp.search.ui.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val viewModel by viewModel<SearchViewModel>()


    private lateinit var binding: FragmentSearchBinding
    private var userText = ""

    private val trackAdapter = TrackAdapter {
        if (clickDebounce()) {
            viewModel.onTrackClicked(it)
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private var textWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.trackRecyclerView.adapter = trackAdapter

        setupListeners()
        viewModel.searchState.observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.toastState.observe(viewLifecycleOwner) {
            it?.let { showToast(it) }
        }
        viewModel.navigateToPlayer.observe(viewLifecycleOwner) { track ->
            findNavController().navigate(R.id.action_searchFragment_to_audioPlayerFragment, AudioPlayerFragment.createArgs(track))
        }
        viewModel.onCreate()
    }

    private fun setupListeners() {

        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onSearchFocusChanged(hasFocus)
        }

        binding.clearIcon.setOnClickListener {
            viewModel.onClearButtonClicked()
            binding.inputEditText.setText("")
            binding.clearIcon.visibility = View.GONE
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
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

    override fun onDestroyView() {
        super.onDestroyView()
        textWatcher?.let { binding.inputEditText.removeTextChangedListener(it) }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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

