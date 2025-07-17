package com.example.playlistmakerapp.media.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.databinding.FragmentFavouriteBinding
import com.example.playlistmakerapp.media.ui.viewmodel.FavouritesState
import com.example.playlistmakerapp.media.ui.viewmodel.FavouritesViewModel
import com.example.playlistmakerapp.player.ui.fragment.AudioPlayerFragment
import com.example.playlistmakerapp.search.ui.TrackAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouriteFragment : Fragment() {

    private val favouritesViewModel: FavouritesViewModel by viewModel()

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!
    private var isClickAllowed = true
    private lateinit var adapter: TrackAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TrackAdapter { track ->
            if (clickDebounce()) {
                parentFragment?.findNavController()?.navigate(
                    R.id.action_mediaFragment_to_audioPlayerFragment,
                    AudioPlayerFragment.createArgs(track)
                )
            }
        }
        binding.recyclerView.adapter = adapter

        favouritesViewModel.favouritesState.observe(viewLifecycleOwner) { state ->
            updateUI(state)
        }

    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        isClickAllowed = true
    }

    private fun clickDebounce(): Boolean {
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    delay(CLICK_DEBOUNCE_DELAY)
                } finally {
                    isClickAllowed = true
                }
            }
            return true
        }
        return false
    }

    private fun updateUI(favouritesState: FavouritesState) {
        when (favouritesState) {
            is FavouritesState.Empty -> {

                binding.errorMessage.visibility = View.VISIBLE
                binding.errorImage.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }

            is FavouritesState.Content -> {
                adapter.tracks.clear()
                adapter.tracks.addAll(favouritesState.favouritesList)
                adapter.notifyDataSetChanged()
                binding.errorMessage.visibility = View.GONE
                binding.errorImage.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }
    }

    companion object {

        private const val CLICK_DEBOUNCE_DELAY = 1000L

        fun newInstance(): Fragment {
            return FavouriteFragment().apply {
                arguments = bundleOf()
            }
        }
    }

}