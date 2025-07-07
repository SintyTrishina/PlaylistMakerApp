package com.example.playlistmakerapp.player.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.databinding.FragmentAudioPlayerBinding
import com.example.playlistmakerapp.player.ui.viewmodel.AudioPlayerState
import com.example.playlistmakerapp.player.ui.viewmodel.AudioPlayerViewModel
import com.example.playlistmakerapp.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerFragment : Fragment() {

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding: FragmentAudioPlayerBinding get() = _binding!!

    private val viewModel by viewModel<AudioPlayerViewModel>()
    private var currentTrack: Track? = null

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

        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        // Получение данных о треке
        currentTrack = arguments?.getParcelable<Track>(TRACK_KEY) ?: run {
            findNavController().navigateUp()
            return
        }

        // Настройка UI
        setupTrackInfo(currentTrack!!)

        // Загрузка состояния "лайка"
        currentTrack?.let { track ->
            viewModel.checkIsFavourite(track.trackId)
        }

        // Подготовка плеера
        currentTrack?.previewUrl?.let { viewModel.setDataSource(it) }

        // Наблюдение за состоянием экрана
        viewModel.playerState.observe(viewLifecycleOwner) { state ->
            updateUI(state)
        }

        viewModel.isFavourite.observe(viewLifecycleOwner) { isLiked ->
            currentTrack?.isFavourite = isLiked
            updateLikeButton(isLiked)
        }

        // Обработка нажатия на кнопку воспроизведения
        binding.buttonPlay.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.buttonLike.setOnClickListener {
            currentTrack?.let { track ->
                viewModel.onFavouriteClicked(track)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateLikeButton(isLiked: Boolean) {
        binding.buttonLike.setImageResource(
            if (isLiked) R.drawable.active_like else R.drawable.like
        )
    }

    private fun setupTrackInfo(track: Track) {
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

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    companion object {
        private const val TRACK_KEY = "track_key"
        fun createArgs(trackKey: Track): Bundle = bundleOf(TRACK_KEY to trackKey)
    }
}