package com.example.playlistmakerapp.search.ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.databinding.FragmentSearchBinding
import com.example.playlistmakerapp.player.ui.fragment.AudioPlayerFragment
import com.example.playlistmakerapp.search.domain.models.Track
import com.example.playlistmakerapp.search.ui.SearchState
import com.example.playlistmakerapp.search.ui.TrackAdapter
import com.example.playlistmakerapp.search.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel by viewModel<SearchViewModel>()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var isClickAllowed = true
    private var textWatcher: TextWatcher? = null

    private val trackAdapter = TrackAdapter { track ->
        if (clickDebounce()) {
            viewModel.onTrackClicked(track)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
        restoreSearchText()
    }

    private fun setupRecyclerView() {
        binding.trackRecyclerView.adapter = trackAdapter
    }

    private fun setupListeners() {
        setupSearchInputListeners()
        setupClearButtonListener()
        setupCleanHistoryListener()
        setupUpdateButtonListener()
    }

    private fun setupSearchInputListeners() {
        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onSearchFocusChanged(hasFocus)
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handleTextChanged(s)
            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.updateSearchText(s?.toString() ?: "")
                // Проверяем нужно ли показывать историю при изменении текста
                if (binding.inputEditText.hasFocus() && s.isNullOrEmpty()) {
                    viewModel.onSearchFocusChanged(true)
                } else {
                    viewModel.onSearchFocusChanged(false)
                }
            }
        }
        textWatcher?.let { binding.inputEditText.addTextChangedListener(it) }
    }

    private fun handleTextChanged(s: CharSequence?) {
        // Управление видимостью иконки очистки
        binding.clearIcon.isVisible = !s.isNullOrEmpty()

        // Запуск поиска с дебаунсом
        viewModel.searchDebounce(changedText = s?.toString() ?: "")

        // Управление отображением истории/результатов
        if (binding.inputEditText.hasFocus() && s.isNullOrEmpty()) {
            viewModel.showSearchHistory()
        }

        // Скрытие плейсхолдера при пустом вводе
        if (s.isNullOrEmpty()) {
            binding.placeholderImage.visibility = View.GONE
            binding.placeholderMessage.visibility = View.GONE
        }
    }

    private fun setupClearButtonListener() {
        binding.clearIcon.setOnClickListener {
            clearSearch()
        }
    }

    private fun clearSearch() {
        viewModel.onClearButtonClicked()
        binding.inputEditText.setText("")
        binding.clearIcon.visibility = View.GONE
        hideKeyboard()
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
    }

    private fun setupCleanHistoryListener() {
        binding.searchHistoryCleanButton.setOnClickListener {
            viewModel.onCleanHistoryClicked()
        }
    }

    private fun setupUpdateButtonListener() {
        binding.updateButton.setOnClickListener {
            viewModel.onUpdateButtonClicked()
        }
    }

    private fun observeViewModel() {
        viewModel.searchState.observe(viewLifecycleOwner) { state ->
            render(state)
        }

        viewModel.shouldShowHistory.observe(viewLifecycleOwner) { shouldShow ->
            if (shouldShow && binding.inputEditText.text.isNullOrEmpty()) {
                viewModel.showSearchHistory()
            }
        }

        viewModel.toastState.observe(viewLifecycleOwner) { message ->
            message?.let { showToast(it) }
        }

        viewModel.navigateToPlayer.observe(viewLifecycleOwner) { track ->
            navigateToPlayer(track)
        }
    }

    private fun navigateToPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_searchFragment_to_audioPlayerFragment,
            AudioPlayerFragment.createArgs(track)
        )
    }

    private fun restoreSearchText() {
        viewModel.lastSearchText?.let { text ->
            if (binding.inputEditText.text.toString() != text) {
                binding.inputEditText.setText(text)
            }
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textWatcher?.let { binding.inputEditText.removeTextChangedListener(it) }
        _binding = null
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
        with(binding) {
            progressBar.visibility = View.VISIBLE
            placeholderImage.visibility = View.GONE
            updateButton.visibility = View.GONE
            searchHint.visibility = View.GONE
            searchHistoryCleanButton.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
            trackRecyclerView.visibility = View.INVISIBLE
        }
    }

    private fun showError(errorMessage: String) {
        with(binding) {
            progressBar.visibility = View.GONE
            placeholderImage.setImageResource(R.drawable.errorconnection)
            placeholderImage.visibility = View.VISIBLE
            updateButton.visibility = View.VISIBLE
            searchHint.visibility = View.GONE
            searchHistoryCleanButton.visibility = View.GONE
            placeholderMessage.visibility = View.VISIBLE
            placeholderMessage.text = errorMessage
        }
    }

    private fun showEmpty(emptyMessage: String) {
        with(binding) {
            progressBar.visibility = View.GONE
            placeholderImage.setImageResource(R.drawable.error)
            placeholderImage.visibility = View.VISIBLE
            updateButton.visibility = View.GONE
            searchHint.visibility = View.GONE
            searchHistoryCleanButton.visibility = View.GONE
            placeholderMessage.visibility = View.VISIBLE
            placeholderMessage.text = emptyMessage
        }
    }

    private fun showContent(tracks: List<Track>) {
        with(binding) {
            progressBar.visibility = View.GONE
            trackRecyclerView.visibility = View.VISIBLE
            placeholderImage.visibility = View.GONE
            updateButton.visibility = View.GONE
            searchHint.visibility = View.GONE
            searchHistoryCleanButton.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
        }
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    private fun showHistory(history: List<Track>) {
        with(binding) {
            progressBar.visibility = View.GONE
            placeholderImage.visibility = View.GONE
            updateButton.visibility = View.GONE
            searchHint.visibility = View.VISIBLE
            searchHistoryCleanButton.visibility = View.VISIBLE
            placeholderMessage.visibility = View.GONE
        }
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(history)
        trackAdapter.notifyDataSetChanged()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
