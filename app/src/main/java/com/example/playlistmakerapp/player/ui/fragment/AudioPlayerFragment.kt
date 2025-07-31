package com.example.playlistmakerapp.player.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.databinding.FragmentAudioPlayerBinding
import com.example.playlistmakerapp.player.ui.PlaylistBehaviorAdapter
import com.example.playlistmakerapp.player.ui.viewmodel.AudioPlayerState
import com.example.playlistmakerapp.player.ui.viewmodel.AudioPlayerViewModel
import com.example.playlistmakerapp.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerFragment : Fragment() {

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding: FragmentAudioPlayerBinding get() = _binding!!

    private val viewModel by viewModel<AudioPlayerViewModel>()
    private var currentTrack: Track? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var playlistsAdapter: PlaylistBehaviorAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        initBottomSheet()
        setupClickListeners()
        observeViewModel()
        setupTrackInfo()
    }

    private fun setupAdapters() {
        playlistsAdapter = PlaylistBehaviorAdapter(
            onPlaylistClickListener = { playlist ->
                currentTrack?.let { track ->
                    viewModel.addTrackToPlaylist(track, playlist)
                }
            },
            loadImage = { path ->
                try {
                    Glide.with(requireContext())
                        .asBitmap()
                        .load(path)
                        .submit()
                        .get()
                } catch (e: Exception) {
                    null
                }
            }
        )
        binding.recyclerView.apply {
            adapter = playlistsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            peekHeight = 0
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> binding.overlay.visibility = View.GONE
                        else -> binding.overlay.visibility = View.VISIBLE
                    }
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        viewModel.loadPlaylists()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }

        binding.overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun setupClickListeners() {
        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonAdd.setOnClickListener {
            viewModel.loadPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.createPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_audioPlayerFragment_to_newPlaylistFragment)
                .also {

                    findNavController().currentBackStackEntry
                        ?.savedStateHandle
                        ?.getLiveData<Boolean>("playlist_created")
                        ?.observe(viewLifecycleOwner) { created ->
                            if (created) {
                                viewModel.loadPlaylists()
                            }
                        }
                }
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.buttonPlay.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.buttonLike.setOnClickListener {
            currentTrack?.let { track ->
                viewModel.onFavouriteClicked(track)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.playerState.observe(viewLifecycleOwner) { state ->
            updateUI(state)
        }

        viewModel.isFavourite.observe(viewLifecycleOwner) { isLiked ->
            currentTrack?.isFavourite = isLiked
            updateLikeButton(isLiked)
        }

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlistsAdapter.playlists = playlists
        }

        viewModel.addToPlaylistStatus.observe(viewLifecycleOwner) { status ->
            status?.let {
                when (it) {
                    is AudioPlayerViewModel.AddToPlaylistStatus.Success -> {
                        showToast("Добавлено в плейлист ${it.playlistName}")
                        viewModel.loadPlaylists()
                    }

                    is AudioPlayerViewModel.AddToPlaylistStatus.AlreadyExists -> {
                        showToast("Трек уже добавлен в плейлист ${it.playlistName}")
                    }
                }
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                viewModel.addToPlaylistStatusHandled()
            }
        }
    }


    private fun setupTrackInfo() {
        currentTrack = arguments?.getParcelable<Track>(TRACK_KEY) ?: run {
            findNavController().navigateUp()
            return
        }

        currentTrack?.let { track ->
            binding.trackName.text = track.trackName
            binding.artistName.text = track.artistName
            binding.trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault())
                .format(track.trackTimeMillis)

            if (track.collectionName.isEmpty()) {
                binding.collectionName.visibility = View.GONE
            } else {
                binding.collectionName.text = track.collectionName
            }

            val year = track.releaseDate.substring(0, 4)
            binding.releaseDate.text = year

            binding.primaryGenreName.text = track.primaryGenreName
            binding.country.text = track.country

            val cornerRadius = dpToPx(8f, requireContext())
            Glide.with(binding.imageMusic.context)
                .load(track.getCoverArtwork())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(cornerRadius))
                .into(binding.imageMusic)

            viewModel.checkIsFavourite(track.trackId)
            track.previewUrl?.let { viewModel.setDataSource(it) }
        }
    }

    private fun updateUI(state: AudioPlayerState) {
        when (state) {
            is AudioPlayerState.Default -> {
                binding.buttonPlay.isEnabled = state.isPlayButtonEnabled
            }

            is AudioPlayerState.Prepared -> {
                binding.buttonPlay.isEnabled = state.isPlayButtonEnabled
                binding.buttonPlay.setImageResource(R.drawable.black_button)
                binding.timePlay.setText(R.string.timer)
            }

            is AudioPlayerState.Playing -> {
                binding.buttonPlay.isEnabled = state.isPlayButtonEnabled
                binding.buttonPlay.setImageResource(R.drawable.pause_button)
                binding.timePlay.text = state.currentPosition
            }

            is AudioPlayerState.Paused -> {
                binding.buttonPlay.isEnabled = state.isPlayButtonEnabled
                binding.buttonPlay.setImageResource(R.drawable.black_button)
                binding.timePlay.text = state.currentPosition
            }
        }
    }

    private fun updateLikeButton(isLiked: Boolean) {
        binding.buttonLike.setImageResource(
            if (isLiked) R.drawable.active_like else R.drawable.like
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TRACK_KEY = "track_key"
        fun createArgs(trackKey: Track): Bundle = bundleOf(TRACK_KEY to trackKey)
    }
}