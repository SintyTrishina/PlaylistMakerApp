package com.example.playlistmakerapp.media.ui.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.databinding.FragmentPlaylistInfoBinding
import com.example.playlistmakerapp.media.ui.viewmodel.PlaylistInfoViewModel
import com.example.playlistmakerapp.media.ui.viewmodel.PlaylistState
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistInfoFragment : Fragment() {
    private var _binding: FragmentPlaylistInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistInfoViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistInfoBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = arguments?.getLong("playlistId") ?: -1L


        binding.toolBar.setOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.playlistState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistState.Content -> showContent(state)
                is PlaylistState.Error -> showError(state.message)
            }
        }

        viewModel.loadPlaylistInfo(playlistId)
    }


    private fun showContent(state: PlaylistState.Content) {
        with(binding) {

            playlistName.text = state.playlist.name
            playlistDescription.text = state.playlist.description


            minutes.text = "${state.duration}"
            counts.text = "${convertTrackCountText(state.tracksCount)}"

            state.playlist.imagePath?.let { imagePath ->
                loadPlaylistImage(imagePath)
            } ?: run {
                imagePlaylist.setImageResource(R.drawable.placeholder)
            }
        }
    }

    private fun loadPlaylistImage(imagePath: String) {
        try {

            val file = if (imagePath.contains("playlist_covers")) {
                File(imagePath)
            } else {
                File(requireContext().filesDir, "playlist_covers/$imagePath")
            }

            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                if (bitmap != null) {
                    binding.imagePlaylist.setImageBitmap(bitmap)
                    return
                }
            }
            binding.imagePlaylist.setImageResource(R.drawable.placeholder)
        } catch (e: Exception) {
            binding.imagePlaylist.setImageResource(R.drawable.placeholder)
        }
    }

    private fun convertTrackCountText(count: Int): String {
        return when {
            count % 100 in 11..14 -> "$count треков"
            count % 10 == 1 -> "$count трек"
            count % 10 in 2..4 -> "$count трека"
            else -> "$count треков"
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}