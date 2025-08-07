package com.example.playlistmakerapp.media.ui.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.databinding.FragmentPlaylistInfoBinding
import com.example.playlistmakerapp.media.domain.model.Playlist
import com.example.playlistmakerapp.media.ui.viewmodel.PlaylistInfoViewModel
import com.example.playlistmakerapp.media.ui.viewmodel.PlaylistState
import com.example.playlistmakerapp.search.domain.models.Track
import com.example.playlistmakerapp.search.ui.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.Locale

class PlaylistInfoFragment : Fragment() {
    private var _binding: FragmentPlaylistInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistInfoViewModel by viewModel()

    private val trackAdapter by lazy {
        TrackAdapter { track ->
            findNavController().navigate(
                R.id.action_playlistInfoFragment_to_audioPlayerFragment,
                createArgs(track)
            )
        }.apply {
            setOnLongClickListener { track ->
                showDeleteDialog(track)
            }
        }
    }

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

        binding.recyclerView.adapter = trackAdapter

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

        setupShareButton()

        binding.menuButton.setOnClickListener {
            showMenuBottomSheet()
        }

        binding.share.setOnClickListener {
            shareCurrentPlaylist()
            hideMenuBottomSheet()
        }

        binding.edit.setOnClickListener {
            editCurrentPlaylist()
            hideMenuBottomSheet()
        }

        binding.delete.setOnClickListener {
            hideMenuBottomSheet()
            showDeletePlaylistDialog()
        }
    }

    private fun editCurrentPlaylist() {
        val playlist = (viewModel.playlistState.value as? PlaylistState.Content)?.playlist ?: return
        findNavController().navigate(
            R.id.action_playlistInfoFragment_to_newPlaylistFragment,
            Bundle().apply {
                putLong("playlist_id_key", playlist.id)
            }
        )
    }

    private fun showMenuBottomSheet() {
        val state = viewModel.playlistState.value as? PlaylistState.Content ?: return

        binding.overlay.isVisible = true
        binding.menuBottomSheet.isVisible = true

        binding.behaviorName.text = state.playlist.name
        binding.tracksCount.text = convertTrackCountText(state.tracks.size)

        state.playlist.imagePath?.let { imagePath ->
            try {
                val file = if (imagePath.contains("playlist_covers")) {
                    File(imagePath)
                } else {
                    File(requireContext().filesDir, "playlist_covers/$imagePath")
                }
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    binding.poster.setImageBitmap(bitmap)
                } else {
                    binding.poster.setImageResource(R.drawable.placeholder)
                }
            } catch (e: Exception) {
                binding.poster.setImageResource(R.drawable.placeholder)
            }
        } ?: run {
            binding.poster.setImageResource(R.drawable.placeholder)
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    hideMenuBottomSheet()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun hideMenuBottomSheet() {
        binding.overlay.isVisible = false
        binding.menuBottomSheet.isVisible = false
    }

    private fun shareCurrentPlaylist() {
        val state = viewModel.playlistState.value as? PlaylistState.Content ?: return
        sharePlaylist(state.playlist, state.tracks)
    }

    private fun showDeletePlaylistDialog() {
        val playlist = (viewModel.playlistState.value as? PlaylistState.Content)?.playlist ?: return

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_playlist_title))
            .setMessage(getString(R.string.delete_playlist_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deletePlaylist(playlist)
                findNavController().navigateUp()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun showContent(state: PlaylistState.Content) {
        with(binding) {
            playlistName.text = state.playlist.name
            if (state.playlist.description != null) {
                playlistDescription.isVisible = true
                playlistDescription.text = state.playlist.description
            }

            minutes.text = state.duration
            counts.text = convertTrackCountText(state.tracksCount)

            state.playlist.imagePath?.let { imagePath ->
                loadPlaylistImage(imagePath)
            } ?: run {
                imagePlaylist.setImageResource(R.drawable.placeholder)
            }

            trackAdapter.tracks.clear()
            trackAdapter.tracks.addAll(state.tracks)
            trackAdapter.notifyDataSetChanged()
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
            count % 100 in 11..14 -> "$count ${getString(R.string.tracks)}"
            count % 10 == 1 -> "$count ${getString(R.string.track)}"
            count % 10 in 2..4 -> "$count ${getString(R.string.tracks_genitive)}"
            else -> "$count ${getString(R.string.tracks)}"
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_LONG).show()
    }

    private fun showDeleteDialog(track: Track) {
        val currentPlaylist = (viewModel.playlistState.value as? PlaylistState.Content)?.playlist
            ?: return

        binding.overlay.isVisible = true

        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.delete_track_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.removeTrackFromPlaylist(track, currentPlaylist)
                binding.overlay.isVisible = false
            }
            .setNegativeButton(getString(R.string.no)) { _, _ ->
                binding.overlay.isVisible = false
            }
            .setOnDismissListener {
                binding.overlay.isVisible = false
            }
            .show()
    }

    private fun setupShareButton() {
        binding.shareButton.setOnClickListener {
            val state = viewModel.playlistState.value
            if (state is PlaylistState.Content) {
                if (state.tracks.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.empty_playlist_share_message),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    sharePlaylist(state.playlist, state.tracks)
                }
            }
        }
    }

    private fun sharePlaylist(playlist: Playlist, tracks: List<Track>) {
        val shareText = buildShareText(playlist, tracks)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_playlist_title)))
    }

    private fun buildShareText(playlist: Playlist, tracks: List<Track>): String {
        val stringBuilder = StringBuilder()

        stringBuilder.append(playlist.name).append("\n")

        playlist.description?.let {
            stringBuilder.append(it).append("\n")
        }

        stringBuilder.append(convertTrackCountText(tracks.size)).append("\n\n")

        tracks.forEachIndexed { index, track ->
            stringBuilder.append(
                "${index + 1}. ${track.artistName} - ${track.trackName} (${
                    formatTrackDuration(
                        track.trackTimeMillis
                    )
                })\n"
            )
        }

        return stringBuilder.toString()
    }

    private fun formatTrackDuration(millis: Long?): String {
        if (millis == null) return "0:00"

        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TRACK_KEY = "track_key"
        fun createArgs(track: Track): Bundle = Bundle().apply {
            putParcelable(TRACK_KEY, track)
        }
    }
}