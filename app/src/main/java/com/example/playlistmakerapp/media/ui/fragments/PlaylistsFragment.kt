package com.example.playlistmakerapp.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.playlistmakerapp.databinding.FragmentPlaylistsBinding
import com.example.playlistmakerapp.media.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistsFragment : Fragment() {

    companion object {
        private const val PLAYLIST_ID = "playlistId"

        fun newInstance(playlistId: Int): Fragment {
            return PlaylistsFragment().apply {
                arguments = bundleOf(PLAYLIST_ID to playlistId)
            }
        }
    }

    private val playlistsViewModel: PlaylistsViewModel by viewModel {
        parametersOf(
            requireArguments().getInt(
                PLAYLIST_ID
            )
        )
    }

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistsViewModel.playlistIdLiveData.observe(viewLifecycleOwner) {
            showError()
        }

    }

    private fun showError() {
        binding.playlistsRoot.visibility = View.VISIBLE
    }
}