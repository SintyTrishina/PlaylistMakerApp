package com.example.playlistmakerapp.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.playlistmakerapp.databinding.FragmentFavouriteBinding
import com.example.playlistmakerapp.media.ui.viewmodel.FavouritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavouriteFragment : Fragment() {

    private val favouritesViewModel: FavouritesViewModel by viewModel {
        parametersOf(
            requireArguments().getInt(
                TRACK_ID
            )
        )
    }

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

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

        favouritesViewModel.favouritesState.observe(viewLifecycleOwner) {
            showError()
        }

    }

    private fun showError() {
        binding.favouriteRoot.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TRACK_ID = "trackId"

        fun newInstance(trackId: Int): Fragment {
            return FavouriteFragment().apply {
                arguments = bundleOf(TRACK_ID to trackId)
            }
        }
    }

}